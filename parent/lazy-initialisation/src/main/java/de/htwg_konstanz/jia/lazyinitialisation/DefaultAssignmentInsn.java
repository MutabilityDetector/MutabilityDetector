/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import static org.apache.commons.lang3.Validate.notNull;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LabelNode;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 11.02.2013
 */
@Immutable
final class DefaultAssignmentInsn implements AssignmentInsn {

    private final LabelNode labelNode;
    private final int indexOfAssignmentInsn;
    private final FieldInsnNode assignmentInsnNode;

    private DefaultAssignmentInsn(final LabelNode theLabelNode, final int theIndexOfAssignmentInsn,
            final FieldInsnNode theAssignmentInsnNode) {
        labelNode = deepCopy(theLabelNode);
        indexOfAssignmentInsn = theIndexOfAssignmentInsn;
        assignmentInsnNode = deepCopy(theAssignmentInsnNode);
    }

    private static LabelNode deepCopy(final LabelNode source) {
        final LabelNode result;
        if (null == source) {
            result = new LabelNode(null);
        } else {
            result = new LabelNode(source.getLabel());
        }
        return result;
    }

    private static FieldInsnNode deepCopy(final FieldInsnNode source) {
        return new FieldInsnNode(source.getOpcode(), source.owner, source.name, source.desc);
    }

    public static AssignmentInsn getInstance(final FieldInsnNode assignmentInsnNode) {
        return new DefaultAssignmentInsn(null, Integer.MIN_VALUE, notNull(assignmentInsnNode));
    }

    public static AssignmentInsn getInstance(final FieldInsnNode assignmentInsnNode,
            final int indexOfAssignmentInsn) {
        return new DefaultAssignmentInsn(null, indexOfAssignmentInsn, notNull(assignmentInsnNode));
    }

    public static AssignmentInsn getInstance(final LabelNode labelNode, final FieldInsnNode assignmentInsnNode) {
        return new DefaultAssignmentInsn(labelNode, Integer.MIN_VALUE, notNull(assignmentInsnNode));
    }

    public static AssignmentInsn getInstance(final LabelNode labelNode, final int indexOfAssignmentInsn,
            final FieldInsnNode assignmentInsnNode) {
        return new DefaultAssignmentInsn(labelNode, indexOfAssignmentInsn, notNull(assignmentInsnNode));
    }

    /**
     * @return the index of this assignment instruction within the set of
     *         instructions of a setter (method oder constructor).
     */
    @Override
    public int getIndexOfAssignmentInstruction() {
        return indexOfAssignmentInsn;
    }

    @Override
    public String getNameOfAssignedVariable() {
        return assignmentInsnNode.name;
    }

    @Override
    public FieldInsnNode getAssignmentInstructionNode() {
        return deepCopy(assignmentInsnNode);
    }

    /**
     * @param labelNodeToCheckFor
     *            the {@code LabelNode} for which it is checked whether this
     *            assignment instruction is placed under this label.
     * @return {@code true} if this assignment instruction is placed under
     *         {@code labelNodeToCheckFor}, {@code false} else.
     */
    @Override
    public boolean isUnderLabel(final LabelNode labelNodeToCheckFor) {
        return labelNode.equals(labelNodeToCheckFor);
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + assignmentInsnNode.hashCode();
        result = prime * result + indexOfAssignmentInsn;
        result = prime * result + labelNode.hashCode();
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
        if (!(obj instanceof DefaultAssignmentInsn)) {
            return false;
        }
        final DefaultAssignmentInsn other = (DefaultAssignmentInsn) obj;
        if (assignmentInsnNode == null) {
            if (other.assignmentInsnNode != null) {
                return false;
            }
        } else if (!assignmentInsnNode.equals(other.assignmentInsnNode)) {
            return false;
        }
        if (indexOfAssignmentInsn != other.indexOfAssignmentInsn) {
            return false;
        }
        if (!labelNode.equals(other.labelNode)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("labelNode", labelNode).append("indexOfAssignmentInstruction", indexOfAssignmentInsn);
        builder.append("assignmentInstructionNode", assignmentInsnNode);
        return builder.toString();
    }

}
