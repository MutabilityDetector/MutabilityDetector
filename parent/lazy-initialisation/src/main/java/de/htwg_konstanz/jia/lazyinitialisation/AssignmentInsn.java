package de.htwg_konstanz.jia.lazyinitialisation;

import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LabelNode;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 15.02.2013
 */
interface AssignmentInsn {

    /**
     * @return the index of this assignment instruction within the set of
     *         instructions of a setter (method oder constructor).
     */
    int getIndexOfAssignmentInstruction();

    String getNameOfAssignedVariable();

    FieldInsnNode getAssignmentInstructionNode();

    /**
     * @param labelNodeToCheckFor
     *            the {@code LabelNode} for which it is checked whether this
     *            assignment instruction is placed under this label.
     * @return {@code true} if this assignment instruction is placed under
     *         {@code labelNodeToCheckFor}, {@code false} else.
     */
    boolean isUnderLabel(LabelNode labelNodeToCheckFor);

    boolean isNull();

    int hashCode();

    boolean equals(Object obj);

}
