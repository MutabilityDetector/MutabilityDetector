package org.mutabilitydetector.checkers.settermethod;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;

/**
 * @author Juergen Fickel
 * @version 19.02.2013
 */
final class NullJumpInsn implements JumpInsn {

    private static final class InstanceHolder {
        private static final JumpInsn INSTANCE = new NullJumpInsn();
    }

    private static final JumpInsnNode EMPTY_JUMP_INSN_NODE = new JumpInsnNode(Opcodes.NOP, null);

    private NullJumpInsn() {
        super();
    }

    public static JumpInsn getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public int compareTo(final JumpInsn o) {
        return -1;
    }

    @Override
    public JumpInsnNode getJumpInsnNode() {
        return EMPTY_JUMP_INSN_NODE;
    }

    @Override
    public int getIndexWithinBlock() {
        return Integer.MIN_VALUE;
    }

    @Override
    public int getIndexWithinMethod() {
        return Integer.MIN_VALUE;
    }

    @Override
    public LabelNode getLabelNodeOfJumpTarget() {
        return null;
    }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public boolean isAssignmentGuard() {
        return false;
    }

    @Override
    public String toString() {
        return "NullJumpInsn []";
    }

}
