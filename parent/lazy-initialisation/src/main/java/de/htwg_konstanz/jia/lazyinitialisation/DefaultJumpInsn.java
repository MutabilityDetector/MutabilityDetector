/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 15.02.2013
 */
final class DefaultJumpInsn implements JumpInsn {

    private final JumpInsnNode jumpInsnNode;
    private final int indexOfJumpInsn;

    private DefaultJumpInsn(final JumpInsnNode theJumpInsnNode, final int theIndexOfJumpInsn) {
        jumpInsnNode = deepCopy(theJumpInsnNode);
        indexOfJumpInsn = theIndexOfJumpInsn;
    }

    private static JumpInsnNode deepCopy(final JumpInsnNode source) {
        final int resultOpcode = source.getOpcode();
        final LabelNode sourceLabelNode = source.label;
        final LabelNode resultLabelNode = new LabelNode(sourceLabelNode.getLabel());
        return new JumpInsnNode(resultOpcode, resultLabelNode);
    }

    @Override
    public LabelNode getLabelNode() {
        return new LabelNode(jumpInsnNode.label.getLabel());
    }

    @Override
    public JumpInsnNode getJumpInsnNode() {
        return deepCopy(jumpInsnNode);
    }

    @Override
    public int getIndexOfJumpInsn() {
        return indexOfJumpInsn;
    }

    @Override
    public LabelNode getTargetLabelNode() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AssignmentInsn getTargetAssignmentInsn() {
        // TODO Auto-generated method stub
        return null;
    }

}
