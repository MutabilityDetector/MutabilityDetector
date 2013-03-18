package org.mutabilitydetector.checkers.settermethod;

import org.objectweb.asm.tree.FieldInsnNode;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 15.02.2013
 */
interface AssignmentInsn {

    /**
     * @return the index of this assignment instruction within the set of
     *         instructions of a setter (method oder constructor).
     */
    int getIndexWithinMethod();

    String getNameOfAssignedVariable();

    FieldInsnNode getAssignmentInstructionNode();

    ControlFlowBlock getSurroundingControlFlowBlock();

    boolean isNull();

    int hashCode();

    boolean equals(Object obj);

}
