/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import static org.apache.commons.lang3.Validate.notNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LabelNode;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 11.02.2013
 */
final class AssignmentInsn {

    private final LabelNode labelNode;
    private final FieldInsnNode assignmentInstructionNode;

    private AssignmentInsn(final LabelNode theLabelNode, final FieldInsnNode theSetInstructionNode) {
        labelNode = theLabelNode;
        assignmentInstructionNode = theSetInstructionNode;
    }

    public static AssignmentInsn getInstance(final LabelNode labelNode,
            final FieldInsnNode assignmentInstructionNode) {
        return new AssignmentInsn(notNull(labelNode), notNull(assignmentInstructionNode));
    }

    public String getNameOfAssignedVariable() {
        return assignmentInstructionNode.name;
    }

    public FieldInsnNode getInstructionNode() {
        return assignmentInstructionNode;
    }

    public boolean isUnderLabel(final LabelNode labelNodeToCheckFor) {
        return labelNode.equals(labelNodeToCheckFor);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + labelNode.hashCode();
        result = prime * result + assignmentInstructionNode.hashCode();
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
        if (!(obj instanceof AssignmentInsn)) {
            return false;
        }
        final AssignmentInsn other = (AssignmentInsn) obj;
        if (!labelNode.equals(other.labelNode)) {
            return false;
        }
        if (!assignmentInstructionNode.equals(other.assignmentInstructionNode)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("labelNode", labelNode).append("assignmentInstructionNode", assignmentInstructionNode);
        return builder.toString();
    }

}
