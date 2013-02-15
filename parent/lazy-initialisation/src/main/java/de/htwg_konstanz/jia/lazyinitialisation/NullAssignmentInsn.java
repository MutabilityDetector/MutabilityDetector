/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LabelNode;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 15.02.2013
 */
public final class NullAssignmentInsn implements AssignmentInsn {

    private static final class InstanceHolder {
        private static final NullAssignmentInsn INSTANCE = new NullAssignmentInsn();
    }

    private NullAssignmentInsn() {
        super();
    }

    public static AssignmentInsn getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public int getIndexOfAssignmentInstruction() {
        return Integer.MIN_VALUE;
    }

    @Override
    public String getNameOfAssignedVariable() {
        return "";
    }

    @Override
    public FieldInsnNode getAssignmentInstructionNode() {
        return new FieldInsnNode(Integer.MIN_VALUE, "", "", "");
    }

    @Override
    public boolean isUnderLabel(final LabelNode labelNodeToCheckFor) {
        return false;
    }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this);
        return builder.toString();
    }

}
