package org.mutabilitydetector.checkers.settermethod;

import static java.lang.String.format;
import static org.mutabilitydetector.checkers.AccessModifierQuery.field;
import static org.mutabilitydetector.locations.ClassLocation.fromInternalName;
import static org.mutabilitydetector.locations.FieldLocation.fieldLocation;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.NotThreadSafe;

import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.Reason;
import org.mutabilitydetector.checkers.AbstractMutabilityChecker;
import org.mutabilitydetector.locations.ClassLocation;
import org.mutabilitydetector.locations.FieldLocation;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 13.03.2013
 */
@NotThreadSafe
abstract class AbstractSetterMethodChecker extends AbstractMutabilityChecker {

    protected CandidatesInitialisersMapping candidatesInitialisersMapping;
    private final ClassNode classNode;
    @GuardedBy("this")
    private volatile EnhancedClassNode enhancedClassNode;

    public AbstractSetterMethodChecker() {
        candidatesInitialisersMapping = CandidatesInitialisersMapping.newInstance();
        classNode = new ClassNode();
        enhancedClassNode = null;
    }

    public final void accept(final ClassVisitor cv) {
        classNode.accept(cv);
    }

    @Override
    public final void visit(final int version, final int access, final String name, final String signature,
            final String superName, final String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        classNode.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public final void visitSource(final String file, final String debug) {
        classNode.visitSource(file, debug);
    }

    @Override
    public final void visitOuterClass(final String owner, final String name, final String desc) {
        classNode.visitOuterClass(owner, name, desc);
    }

    @Override
    public final AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
        return classNode.visitAnnotation(desc, visible);
    }

    @Override
    public final void visitAttribute(final Attribute attr) {
        classNode.visitAttribute(attr);
    }

    @Override
    public final void visitInnerClass(final String name, final String outerName, final String innerName,
            final int access) {
        classNode.visitInnerClass(name, outerName, innerName, access);
    }

    @Override
    public final FieldVisitor visitField(final int access, final String name, final String desc,
            final String signature, final Object value) {
        FieldVisitor result = null;
        if (isCandidate(access)) {
            result = new FieldNode(access, name, desc, signature, value);
            candidatesInitialisersMapping.addCandidate((FieldNode) result);
        } else if (field(access).isNotFinal() && field(access).isNotStatic()) {
            setNonFinalFieldResult(name);
        }
        if (null == result) {
            result = classNode.visitField(access, name, desc, signature, value);
        }
        return result;
    }

    private static boolean isCandidate(final int access) {
        return field(access).isPrivate() && field(access).isNotFinal();
    }

    @Override
    public final MethodVisitor visitMethod(final int access, final String name, final String desc,
            final String signature, final String[] exceptions) {
        return classNode.visitMethod(access, name, desc, signature, exceptions);
    }

    @Override
    public final void visitEnd() {
        classNode.visitEnd();
        verify();
    }

    /**
     * Template method for verification of lazy initialisation.
     */
    protected final void verify() {
        collectInitialisers();
        verifyCandidates();
        verifyInitialisers();
        collectPossibleInitialValues();
        verifyPossibleInitialValues();
        collectEffectiveAssignmentInstructions();
        verifyEffectiveAssignmentInstructions();
        collectAssignmentGuards();
        verifyAssignmentGuards();
        end();
    }

    protected abstract void collectInitialisers();

    protected abstract void verifyCandidates();

    protected abstract void verifyInitialisers();

    protected abstract void collectPossibleInitialValues();

    protected abstract void verifyPossibleInitialValues();
    
    protected abstract void collectEffectiveAssignmentInstructions();
    
    protected abstract void verifyEffectiveAssignmentInstructions();

    protected abstract void collectAssignmentGuards();

    protected abstract void verifyAssignmentGuards();

    protected void end() {}

    /**
     * @return
     */
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

    protected void setResultForClass(final String message, final Reason reason) {
        super.setResult(message, fromInternalName(classNode.name), reason);
    }

    final void setNonFinalFieldResult(final String variableName) {
        final String msg = "Field is not final, if shared across threads the Java Memory Model will not"
                + " guarantee it is initialised before it is read.";
        setNonFinalFieldResult(msg, variableName);
    }

    final void setNonFinalFieldResult(final String message, final String variableName) {
        final FieldLocation location = fieldLocation(variableName, ClassLocation.fromInternalName(ownerClass));
        setResult(message, location, MutabilityReason.NON_FINAL_FIELD);
    }

    final void setFieldCanBeReassignedResult(final String variableName, final String methodName) {
        final String msgTemplate = "Field [%s] can be reassigned within method [%s]";
        final String msg = format(msgTemplate, variableName, methodName);
        setFieldCanBeReassignedResult(msg);
    }

    final void setFieldCanBeReassignedResult(final String message) {
        setResultForClass(message, MutabilityReason.FIELD_CAN_BE_REASSIGNED);
    }

    final void setMutableTypeToFieldResult(final String message, final String variableName) {
        final FieldLocation location = fieldLocation(variableName, ClassLocation.fromInternalName(ownerClass));
        setResult(message, location, MutabilityReason.MUTABLE_TYPE_TO_FIELD);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName());
        builder.append(", [candidatesInitialisersMapping=").append(candidatesInitialisersMapping);
        builder.append(", classNode=").append(classNode);
        builder.append(", enhancedClassNode=").append(enhancedClassNode).append("]");
        return builder.toString();
    }

}
