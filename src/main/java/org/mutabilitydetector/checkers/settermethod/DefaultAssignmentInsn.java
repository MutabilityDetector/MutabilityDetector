/**
 * 
 */
package org.mutabilitydetector.checkers.settermethod;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.concurrent.NotThreadSafe;

import org.objectweb.asm.tree.FieldInsnNode;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 11.02.2013
 */
@NotThreadSafe
final class DefaultAssignmentInsn implements AssignmentInsn {

    private final ControlFlowBlock surroundingControlFlowBlock;
    private final int indexWithinMethod;
    private final FieldInsnNode assignmentInsnNode;

    private DefaultAssignmentInsn(final ControlFlowBlock theSurroundingControlFlowBlock, final int theIndexWithinMethod,
            final FieldInsnNode theAssignmentInsnNode) {
        surroundingControlFlowBlock = theSurroundingControlFlowBlock;
        indexWithinMethod = theIndexWithinMethod;
        assignmentInsnNode = theAssignmentInsnNode;
    }

    public static AssignmentInsn newInstance(final ControlFlowBlock surroundingControlFlowBlock,
            final int indexWithinMethod,
            final FieldInsnNode assignmentInsnNode) {
        return new DefaultAssignmentInsn(checkNotNull(surroundingControlFlowBlock), indexWithinMethod,
                checkNotNull(assignmentInsnNode));
    }

    /**
     * @return the index of this assignment instruction within the set of
     *         instructions of a setter (method oder constructor).
     */
    @Override
    public int getIndexWithinMethod() {
        return indexWithinMethod;
    }

    @Override
    public String getNameOfAssignedVariable() {
        return assignmentInsnNode.name;
    }

    @Override
    public ControlFlowBlock getSurroundingControlFlowBlock() {
        return surroundingControlFlowBlock;
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + surroundingControlFlowBlock.hashCode();
        result = prime * result + assignmentInsnNode.hashCode();
        result = prime * result + indexWithinMethod;
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
        if (!surroundingControlFlowBlock.equals(other.surroundingControlFlowBlock)) {
            return false;
        }
        if (assignmentInsnNode == null) {
            if (other.assignmentInsnNode != null) {
                return false;
            }
        } else if (!assignmentInsnNode.equals(other.assignmentInsnNode)) {
            return false;
        }
        if (indexWithinMethod != other.indexWithinMethod) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName());
        builder.append(" [surroundingControlFlowBlock=").append(surroundingControlFlowBlock);
        builder.append(", indexWithinMethod=").append(indexWithinMethod);
        builder.append(", assignmentInstructionNode=");
        final Opcode opcode = Opcode.forInt(assignmentInsnNode.getOpcode());
        builder.append(opcode.name()).append(" ").append(assignmentInsnNode.name).append("]");
        return builder.toString();
    }

}
