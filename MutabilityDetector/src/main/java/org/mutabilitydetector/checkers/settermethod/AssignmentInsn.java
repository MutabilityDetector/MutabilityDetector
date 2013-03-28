package org.mutabilitydetector.checkers.settermethod;

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

    ControlFlowBlock getSurroundingControlFlowBlock();

    boolean isNull();

}
