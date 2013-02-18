/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 11.02.2013
 */
interface JumpInsn extends Comparable<JumpInsn> {

    JumpInsnNode getJumpInsnNode();

    int getIndexOfJumpInsn();

    LabelNode getLabelNodeOfJumpTarget();

    AssignmentInsn getTargetAssignmentInsn();

}
