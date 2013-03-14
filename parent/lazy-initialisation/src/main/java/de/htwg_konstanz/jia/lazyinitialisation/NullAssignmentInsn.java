/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import org.objectweb.asm.tree.FieldInsnNode;

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
    public int getIndexWithinMethod() {
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
    public ControlFlowBlock getSurroundingControlFlowBlock() {
        return null;
    }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        b.append(getClass().getSimpleName()).append(" []");
        return b.toString();
    }

}
