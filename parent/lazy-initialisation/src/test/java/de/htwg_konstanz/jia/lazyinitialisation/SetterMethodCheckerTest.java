package de.htwg_konstanz.jia.lazyinitialisation;

import static java.lang.String.format;
import static org.mutabilitydetector.CheckerRunner.ExceptionPolicy.FAIL_FAST;
import static org.mutabilitydetector.TestUtil.testingVerifierFactory;
import static org.mutabilitydetector.ThreadUnsafeAnalysisSession.createWithCurrentClassPath;
import static org.mutabilitydetector.checkers.AccessModifierQuery.method;
import static org.mutabilitydetector.checkers.SetterMethodChecker.newSetterMethodChecker;
import static org.mutabilitydetector.checkers.info.MethodIdentifier.forMethod;
import static org.mutabilitydetector.locations.ClassLocation.fromInternalName;
import static org.mutabilitydetector.locations.Slashed.slashed;

import java.io.IOException;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.CheckerRunner;
import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.asmoverride.AsmVerifierFactory;
import org.mutabilitydetector.checkers.AbstractMutabilityChecker;
import org.mutabilitydetector.checkers.FieldAssignmentVisitor;
import org.mutabilitydetector.checkers.MethodIs;
import org.mutabilitydetector.checkers.VarStack;
import org.mutabilitydetector.checkers.VarStack.VarStackSnapshot;
import org.mutabilitydetector.checkers.info.MethodIdentifier;
import org.mutabilitydetector.checkers.info.PrivateMethodInvocationInformation;
import org.mutabilitydetector.checkers.info.SessionCheckerRunner;
import org.mutabilitydetector.locations.ClassName;
import org.mutabilitydetector.locations.Dotted;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;

public final class SetterMethodCheckerTest {

    public static final class SetterMethodChecker extends AbstractMutabilityChecker {

        private final PrivateMethodInvocationInformation privateMethodInvocationInfo;
        private final AsmVerifierFactory verifierFactory;

        private SetterMethodChecker(final PrivateMethodInvocationInformation privateMethodInvocationInfo,
                final AsmVerifierFactory verifierFactory) {
            this.privateMethodInvocationInfo = privateMethodInvocationInfo;
            this.verifierFactory = verifierFactory;
        }

        public static SetterMethodChecker newSetterMethodChecker(
                final PrivateMethodInvocationInformation privateMethodInvocationInfo,
                final AsmVerifierFactory verifierFactory) {
            return new SetterMethodChecker(privateMethodInvocationInfo, verifierFactory);
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
