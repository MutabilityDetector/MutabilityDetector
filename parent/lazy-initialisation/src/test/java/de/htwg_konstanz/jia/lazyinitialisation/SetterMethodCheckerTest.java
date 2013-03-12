package de.htwg_konstanz.jia.lazyinitialisation;

import static java.lang.String.format;
import static org.mutabilitydetector.CheckerRunner.ExceptionPolicy.FAIL_FAST;
import static org.mutabilitydetector.TestUtil.testingVerifierFactory;
import static org.mutabilitydetector.ThreadUnsafeAnalysisSession.createWithCurrentClassPath;
import static org.mutabilitydetector.checkers.AccessModifierQuery.method;
import static org.mutabilitydetector.checkers.SetterMethodChecker.newSetterMethodChecker;
import static org.mutabilitydetector.checkers.info.MethodIdentifier.forMethod;
import static org.mutabilitydetector.locations.ClassLocation.fromInternalName;
import static org.mutabilitydetector.locations.FieldLocation.fieldLocation;
import static org.mutabilitydetector.locations.Slashed.slashed;

import java.io.IOException;
import java.util.*;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Before;
import org.junit.Test;
import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.CheckerRunner;
import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.asmoverride.AsmVerifierFactory;
import org.mutabilitydetector.checkers.*;
import org.mutabilitydetector.checkers.VarStack.VarStackSnapshot;
import org.mutabilitydetector.checkers.info.MethodIdentifier;
import org.mutabilitydetector.checkers.info.PrivateMethodInvocationInformation;
import org.mutabilitydetector.checkers.info.SessionCheckerRunner;
import org.mutabilitydetector.locations.ClassLocation;
import org.mutabilitydetector.locations.ClassName;
import org.mutabilitydetector.locations.Dotted;
import org.mutabilitydetector.locations.FieldLocation;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;

import de.htwg_konstanz.jia.lazyinitialisation.VariableInitialisersAssociation.Entry;
import de.htwg_konstanz.jia.lazyinitialisation.VariableInitialisersAssociation.Initialisers;

public final class SetterMethodCheckerTest {

    public static final class OriginalSetterMethodChecker extends AbstractMutabilityChecker {

        private final PrivateMethodInvocationInformation privateMethodInvocationInfo;
        private final AsmVerifierFactory verifierFactory;

        private OriginalSetterMethodChecker(final PrivateMethodInvocationInformation privateMethodInvocationInfo,
                final AsmVerifierFactory verifierFactory) {
            this.privateMethodInvocationInfo = privateMethodInvocationInfo;
            this.verifierFactory = verifierFactory;
        }

        public static OriginalSetterMethodChecker newSetterMethodChecker(
                final PrivateMethodInvocationInformation privateMethodInvocationInfo,
                final AsmVerifierFactory verifierFactory) {
            return new OriginalSetterMethodChecker(privateMethodInvocationInfo, verifierFactory);
        }

        @Override
        public void visit(final int version, final int access, final String name, final String signature,
                final String superName, final String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
        }

        @Override
        public MethodVisitor visitMethod(final int access, final String name, final String desc,
                final String signature, final String[] exceptions) {
            return new SetterAssignmentVisitor(ownerClass, access, name, desc, signature, exceptions, verifierFactory);
        }

        class SetterAssignmentVisitor extends FieldAssignmentVisitor {

            private final VarStack varStack = new VarStack();

            public SetterAssignmentVisitor(final String ownerName, final int access, final String name,
                    final String desc, final String signature, final String[] exceptions,
                    final AsmVerifierFactory verifierFactory) {
                super(ownerName, access, name, desc, signature, exceptions, verifierFactory);
            }

            @Override
            protected void visitFieldAssignmentFrame(final Frame<BasicValue> assignmentFrame,
                    final FieldInsnNode fieldInsnNode, final BasicValue stackValue) {
                if (MethodIs.aConstructor(name) || isInvalidStackValue(stackValue)) {
                    return;
                }
                if (method(access).isStatic()) {
                    detectInStaticMethod(fieldInsnNode);
                } else {
                    detectInInstanceMethod(fieldInsnNode);
                }

            }

            private boolean isOnlyCalledFromConstructor() {
                final MethodIdentifier methodId = forMethod(slashed(owner), name + ":" + desc);
                return privateMethodInvocationInfo.isOnlyCalledFromConstructor(methodId);
            }

            private void detectInStaticMethod(final FieldInsnNode fieldInsnNode) {
                final String ownerOfReassignedField = fieldInsnNode.owner;
                if (reassignedIsThisType(ownerOfReassignedField) && assignmentIsNotOnAParameter(fieldInsnNode)) {
                    setIsImmutableResult(fieldInsnNode.name);
                }
            }

            private boolean assignmentIsNotOnAParameter(final FieldInsnNode fieldInsnNode) {
                /*
                 * This is a temporary hack/workaround. It's quite difficult to
                 * tell for sure if the owner of the reassigned field is a
                 * parameter. But if the type is not included in the parameter
                 * list, we can guess it's not (though it still may be).
                 */
                final boolean reassignmentIsOnATypeIncludedInParameters = desc.contains(fieldInsnNode.owner);

                return reassignmentIsOnATypeIncludedInParameters;
            }

            private boolean reassignedIsThisType(final String ownerOfReassignedField) {
                return owner.compareTo(ownerOfReassignedField) == 0;
            }

            private void detectInInstanceMethod(final FieldInsnNode fieldInsnNode) {
                if (isOnlyCalledFromConstructor()) {
                    return;
                }

                final VarStackSnapshot varStackSnapshot = varStack.next();
                if (varStackSnapshot.thisObjectWasAddedToStack()) {
                    // Throwing an NPE, assuming it's mutable for now.
                    setIsImmutableResult(fieldInsnNode.name);
                }
            }

            @SuppressWarnings("unused")
            private boolean isThisObject(final int indexOfOwningObject) {
                return indexOfOwningObject == 0;
            }

            @Override
            public void visitFieldInsn(final int opcode, final String fieldsOwner, final String fieldName,
                    final String fieldDesc) {
                super.visitFieldInsn(opcode, fieldsOwner, fieldName, fieldDesc);
                if (opcode == Opcodes.PUTFIELD) {
                    varStack.takeSnapshotOfVarsAtPutfield();
                }
            }

            @Override
            public void visitVarInsn(final int opcode, final int var) {
                super.visitVarInsn(opcode, var);
                varStack.visitVarInsn(var);
            }

            private void setIsImmutableResult(final String fieldName) {
                final String message = format("Field [%s] can be reassigned within method [%s]", fieldName, name);
                setResult(message, fromInternalName(owner), MutabilityReason.FIELD_CAN_BE_REASSIGNED);
            }

        } // class SetterAssignmentVisitor

    } // class SetterMethodChecker

    
    static abstract class AbstractSetterMethodChecker extends AbstractMutabilityChecker {

        private final ClassNode classNode;
        @GuardedBy("this") private volatile EnhancedClassNode enhancedClassNode;

        public AbstractSetterMethodChecker() {
            classNode = new ClassNode();
            enhancedClassNode = null;
        }

        public final void check(int api) {
            classNode.check(api);
        }

        public final void accept(final ClassVisitor cv) {
            classNode.accept(cv);
        }

        public final void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
            classNode.visit(version, access, name, signature, superName, interfaces);
        }

        public final void visitSource(String file, String debug) {
            classNode.visitSource(file, debug);
        }

        public final void visitOuterClass(String owner, String name, String desc) {
            classNode.visitOuterClass(owner, name, desc);
        }

        public final AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            return classNode.visitAnnotation(desc, visible);
        }

        public final void visitAttribute(Attribute attr) {
            classNode.visitAttribute(attr);
        }

        public final void visitInnerClass(String name, String outerName, String innerName, int access) {
            classNode.visitInnerClass(name, outerName, innerName, access);
        }

        public final FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            return classNode.visitField(access, name, desc, signature, value);
        }

        public final MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            return classNode.visitMethod(access, name, desc, signature, exceptions);
        }

        public final void visitEnd() {
            classNode.visitEnd();
            verify();
        }

        /**
         * Template method for verification of lazy initialisation.
         */
        protected final void verify() {
            collectCandidates();
            collectInitialisers();
            verifyCandidates();
            verifyInitialisers();
            collectPossibleInitialValues();
            verifyPossibleInitialValues();
            collectAssignmentGuards();
            verifyAssignmentGuards();
            collectAssignmentInstructions();
            verifyAssignmentInstructions();
        }

        protected abstract void collectCandidates();
        protected abstract void collectInitialisers();
        protected abstract void verifyCandidates();
        protected abstract void verifyInitialisers();
        protected abstract void collectPossibleInitialValues();
        protected abstract void verifyPossibleInitialValues();
        protected abstract void collectAssignmentGuards();
        protected abstract void verifyAssignmentGuards();
        protected abstract void collectAssignmentInstructions();
        protected abstract void verifyAssignmentInstructions();

        protected final EnhancedClassNode getEnhancedClassNode() {
            EnhancedClassNode result = enhancedClassNode;
            if (null == result) {
                synchronized (this) {
                    result = enhancedClassNode;
                    if (null == result) {
                        result = EnhancedClassNode.newInstance(classNode);
                        enhancedClassNode = result;
                    }
                }
            }
            return result;
        }

    } // class AbstractSetterMethodChecker


    @NotThreadSafe
    static final class SetterMethodChecker extends AbstractSetterMethodChecker {

        private final Map<FieldNode, Collection<UnknownTypeValue>> initialValues;
        private final Map<FieldNode, Collection<JumpInsn>> assignmentGuards;
        private VariableInitialisersAssociation variableInitialisersAssociation;

        private SetterMethodChecker() {
            super();
            initialValues = new HashMap<FieldNode, Collection<UnknownTypeValue>>();
            assignmentGuards = new HashMap<FieldNode, Collection<JumpInsn>>();
            variableInitialisersAssociation = null;
        }

        public static AsmMutabilityChecker newInstance() {
            return new SetterMethodChecker();
        }

        @Override
        protected void collectCandidates() {
            final Collection<FieldNode> variablesOfAnalysedClass = getEnhancedClassNode().getFields();
            final Finder<VariableInitialisersAssociation> f = CandidatesFinder.newInstance(variablesOfAnalysedClass);
            variableInitialisersAssociation = f.find();
        }

        @Override
        protected void collectInitialisers() {
            final Collection<MethodNode> methodsOfAnalysedClass = getEnhancedClassNode().getMethods();
            final Finder<VariableInitialisersAssociation> f = InitialisersFinder.newInstance(methodsOfAnalysedClass,
                    variableInitialisersAssociation);
            variableInitialisersAssociation = f.find();
        }

        @Override
        protected void verifyCandidates() {
            final Collection<FieldNode> unassociatedVariables = variableInitialisersAssociation
                    .removeAndGetUnassociatedVariables();
            for (final FieldNode unassociatedVariable : unassociatedVariables) {
                setNonFinalFieldResult(unassociatedVariable.name);
            }
        }

        private void setNonFinalFieldResult(final String variableName) {
            final String msg = "Field is not final, if shared across threads the Java Memory Model will not" +
            		" guarantee it is initialised before it is read.";
            final FieldLocation location = fieldLocation(variableName, ClassLocation.fromInternalName(ownerClass));
            setResult(msg, location, MutabilityReason.NON_FINAL_FIELD);
        }

        @Override
        protected void verifyInitialisers() {
            for (final Entry entry : variableInitialisersAssociation) {
                verifyInitialisersFor(entry.getCandidate(), entry.getInitialisers());
            }
        }

        private void verifyInitialisersFor(final FieldNode candidate, final Initialisers allInitialisersForCandidate) {
            final Collection<MethodNode> methodInitialisers = allInitialisersForCandidate.getMethods();    
            if (containsMoreThanOne(methodInitialisers)) {
                setFieldCanBeReassignedResultForEachMethodInitialiser(candidate.name, methodInitialisers);
            }
        }

        private static boolean containsMoreThanOne(final Collection<?> aCollection) {
            return 1 < aCollection.size();
        }

        private void setFieldCanBeReassignedResultForEachMethodInitialiser(final String candidateName,
                final Collection<MethodNode> methodInitialisers) {
            for (final MethodNode methodInitialiser : methodInitialisers) {
                final String msgTemplate = "Field [%s] can be reassigned within method [%s]";
                final String msg = format(msgTemplate, candidateName, methodInitialiser.name);
                setFieldCanBeReassignedResult(msg);
            }
        }

        private void setFieldCanBeReassignedResult(final String message) {
            final String className = getEnhancedClassNode().getName();
            setResult(message, fromInternalName(className), MutabilityReason.FIELD_CAN_BE_REASSIGNED);
        }

        @Override
        protected void collectPossibleInitialValues() {
            for (final Entry entry : variableInitialisersAssociation) {
                final FieldNode candidate = entry.getCandidate();
                final Initialisers initialisers = entry.getInitialisers();
                final Finder<Set<UnknownTypeValue>> f = InitialValueFinder.newInstance(candidate, initialisers);
                initialValues.put(candidate, f.find());
            }
        }

        @Override
        protected void verifyPossibleInitialValues() {
            if (containsMoreThanOne(initialValues)) {
                setFieldCanBeReassignedResultForEachInitialValue();
            }
        }

        private void setFieldCanBeReassignedResultForEachInitialValue() {
            for (final Map.Entry<FieldNode, Collection<UnknownTypeValue>> e : initialValues.entrySet()) {
                final Collection<UnknownTypeValue> initialValuesForCandidate = e.getValue();
                final String msgTmpl = "Field [%s] has too many possible initial values for lazy initialisation: [%s]";
                final String candidateName = e.getKey().name;
                final String initialValues = initialValuesToString(initialValuesForCandidate);
                final String msg = format(msgTmpl, candidateName, initialValues);
                setFieldCanBeReassignedResult(msg);
            }
        }

        private static String initialValuesToString(final Collection<UnknownTypeValue> initialValuesForCandidate) {
            final StringBuilder result = new StringBuilder();
            final String separatorValue = ", ";
            String separator = "";
            for (final UnknownTypeValue initialValue : initialValuesForCandidate) {
                result.append(separator).append(initialValue);
                separator = separatorValue;
            }
            return result.toString();
        }

        private static boolean containsMoreThanOne(Map<?, ?> aMap) {
            return 1 < aMap.size();
        }

        @Override
        protected void collectAssignmentGuards() {
            for (final Entry e : variableInitialisersAssociation) {
                final Initialisers initialisers = e.getInitialisers();
                collectAssignmentGuardsForEachInitialisingMethod(e.getCandidate(), initialisers.getMethods());
            }
        }

        private void collectAssignmentGuardsForEachInitialisingMethod(final FieldNode candidate,
                final Collection<MethodNode> initialisingMethods) {
            for (final MethodNode initialisingMethod : initialisingMethods) {
                final EnhancedClassNode cn = getEnhancedClassNode();
                final Collection<ControlFlowBlock> blocks = cn.getControlFlowBlocksForMethod(initialisingMethod);
                collectAssignmentGuardsForEachControlFlowBlock(candidate, blocks);
            }
        }

        private void collectAssignmentGuardsForEachControlFlowBlock(final FieldNode candidate,
                final Collection<ControlFlowBlock> controlFlowBlocks) {
            for (final ControlFlowBlock controlFlowBlock : controlFlowBlocks) {
                final Finder<JumpInsn> f = AssignmentGuardFinder.newInstance(candidate.name, controlFlowBlock);
                final JumpInsn supposedAssignmentGuard = f.find();
                addToAssignmentGuards(candidate, supposedAssignmentGuard);
            }
        }

        private void addToAssignmentGuards(final FieldNode candidate, final JumpInsn supposedAssignmentGuard) {
            if (supposedAssignmentGuard.isAssignmentGuard()) {
                final Collection<JumpInsn> assignmentGuardsForCandidate;
                if (assignmentGuards.containsKey(candidate)) {
                    assignmentGuardsForCandidate = assignmentGuards.get(candidate);
                } else {
                    final byte expectedMaximum = 3;
                    assignmentGuardsForCandidate = new ArrayList<JumpInsn>(expectedMaximum);
                    assignmentGuards.put(candidate, assignmentGuardsForCandidate);
                }
                assignmentGuardsForCandidate.add(supposedAssignmentGuard);
            }
        }

        @Override
        protected void verifyAssignmentGuards() {
            // TODO Auto-generated method stub
            
        }

        @Override
        protected void collectAssignmentInstructions() {
            // TODO Auto-generated method stub
            
        }

        @Override
        protected void verifyAssignmentInstructions() {
            // TODO Auto-generated method stub
            
        }

        @Override
        public String toString() {
            final StringBuilder b = new StringBuilder();
            b.append(getClass().getSimpleName()).append(" [");
            // TODO Methodenrumpf korrekt implementieren.
            b.append("]");
            return b.toString();
        }
        
    } // class SetterMethodChecker


    private org.mutabilitydetector.checkers.SetterMethodChecker checker;
    private CheckerRunner checkerRunner;
    private AnalysisSession analysisSession;
    private PrivateMethodInvocationInformation info;

    @Before
    public void setUp() {
        checkerRunner = CheckerRunner.createWithCurrentClasspath(FAIL_FAST);
        analysisSession = createWithCurrentClassPath();
        info = new PrivateMethodInvocationInformation(new SessionCheckerRunner(analysisSession, checkerRunner));
        checker = newSetterMethodChecker(info, testingVerifierFactory());
    }

    @Test
    public void verifyJavaLangString() throws IOException {
        final ClassName dotted = Dotted.fromClass(String.class);
        final ClassReader cr = new ClassReader(dotted.asString());
        cr.accept(checker, 0);
        final IsImmutable result = checker.result();
        final Collection<MutableReasonDetail> reasons = checker.reasons();
        System.out.println(result);
    }

}
