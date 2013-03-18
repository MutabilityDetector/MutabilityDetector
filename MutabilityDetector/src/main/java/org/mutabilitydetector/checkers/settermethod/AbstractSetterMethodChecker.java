package org.mutabilitydetector.checkers.settermethod;

import static java.lang.String.format;
import static org.mutabilitydetector.locations.ClassLocation.fromInternalName;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.NotThreadSafe;

import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.Reason;
import org.mutabilitydetector.checkers.AbstractMutabilityChecker;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ClassNode;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 13.03.2013
 */
@NotThreadSafe
abstract class AbstractSetterMethodChecker extends AbstractMutabilityChecker {

    private final ClassNode classNode;
    @GuardedBy("this")
    private volatile EnhancedClassNode enhancedClassNode;

    public AbstractSetterMethodChecker() {
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
        return classNode.visitField(access, name, desc, signature, value);
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
        collectCandidates();
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

    protected abstract void collectCandidates();

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

    protected void setFieldCanBeReassignedResult(final String variableName, final String methodName) {
        final String msgTemplate = "Field [%s] can be reassigned within method [%s]";
        final String msg = format(msgTemplate, variableName, methodName);
        setResultForClass(msg, MutabilityReason.FIELD_CAN_BE_REASSIGNED);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName()).append(" [classNode=").append(classNode);
        builder.append(", enhancedClassNode=").append(enhancedClassNode).append("]");
        return builder.toString();
    }

}
