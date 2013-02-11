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
final class AssignmentInstruction {

    private final LabelNode labelNode;
    private final FieldInsnNode setInstructionNode;

    private AssignmentInstruction(final LabelNode theLabelNode, final FieldInsnNode theSetInstructionNode) {
        labelNode = theLabelNode;
        setInstructionNode = theSetInstructionNode;
    }

    public static AssignmentInstruction getInstance(final LabelNode labelNode,
            final FieldInsnNode setInstructionNode) {
        return new AssignmentInstruction(notNull(labelNode), notNull(setInstructionNode));
    }

    public String getNameOfAssignedVariable() {
        return setInstructionNode.name;
    }

    public FieldInsnNode getMethodNode() {
        return setInstructionNode;
    }

    public boolean isUnderLabel(final LabelNode labelNodeToCheckFor) {
        return labelNode.equals(labelNodeToCheckFor);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + labelNode.hashCode();
        result = prime * result + setInstructionNode.hashCode();
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
        if (!(obj instanceof AssignmentInstruction)) {
            return false;
        }
        final AssignmentInstruction other = (AssignmentInstruction) obj;
        if (!labelNode.equals(other.labelNode)) {
            return false;
        }
        if (!setInstructionNode.equals(other.setInstructionNode)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("labelNode", labelNode).append("setInstructionNode", setInstructionNode);
        return builder.toString();
    }

}
