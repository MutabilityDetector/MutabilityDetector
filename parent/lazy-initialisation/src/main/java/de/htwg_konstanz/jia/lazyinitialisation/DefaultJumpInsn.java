/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import static org.apache.commons.lang3.Validate.notNull;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 15.02.2013
 */
@Immutable
final class DefaultJumpInsn implements JumpInsn {

    private final JumpInsnNode jumpInsnNode;
    private final int indexOfJumpInsn;
    private final AssignmentInsn targetAssignmentInsn;

    private DefaultJumpInsn(final JumpInsnNode theJumpInsnNode,
            final int theIndexOfJumpInsn,
            final AssignmentInsn theTargetAssignmentInsn) {
        jumpInsnNode = deepCopy(theJumpInsnNode);
        indexOfJumpInsn = theIndexOfJumpInsn;
        targetAssignmentInsn = theTargetAssignmentInsn;
    }

    private static JumpInsnNode deepCopy(final JumpInsnNode source) {
        final int resultOpcode = source.getOpcode();
        final LabelNode sourceLabelNode = source.label;
        final LabelNode resultLabelNode = new LabelNode(sourceLabelNode.getLabel());
        return new JumpInsnNode(resultOpcode, resultLabelNode);
    }

    public static DefaultJumpInsn newInstance(final JumpInsnNode jumpInsnNode,
            final int indexOfJumpInsn,
            final AssignmentInsn targetAssignmentInsn) {
        return new DefaultJumpInsn(notNull(jumpInsnNode), indexOfJumpInsn, notNull(targetAssignmentInsn));
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
    public LabelNode getLabelNodeOfJumpTarget() {
        return new LabelNode(jumpInsnNode.label.getLabel());
    }

    @Override
    public AssignmentInsn getTargetAssignmentInsn() {
        return targetAssignmentInsn;
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public int compareTo(final JumpInsn o) {
        final Integer thisIndexOfJumpInsn = Integer.valueOf(indexOfJumpInsn);
        final Integer otherIndexOfJumpInsn = Integer.valueOf(o.getIndexOfJumpInsn());
        return thisIndexOfJumpInsn.compareTo(otherIndexOfJumpInsn);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + indexOfJumpInsn;
        result = prime * result + jumpInsnNode.hashCode();
        result = prime * result + targetAssignmentInsn.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DefaultJumpInsn)) {
            return false;
        }
        final DefaultJumpInsn other = (DefaultJumpInsn) obj;
        if (indexOfJumpInsn != other.indexOfJumpInsn) {
            return false;
        }
        if (!jumpInsnNode.equals(other.jumpInsnNode)) {
            return false;
        }
        if (!targetAssignmentInsn.equals(other.targetAssignmentInsn)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("jumpInsnNode", jumpInsnNode).append("indexOfJumpInsn", indexOfJumpInsn);
        builder.append("targetAssignmentInsn", targetAssignmentInsn);
        return builder.toString();
    }

}
