/**
 * 
 */
package org.mutabilitydetector.checkers.settermethod;

import org.objectweb.asm.tree.JumpInsnNode;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 11.02.2013
 */
interface JumpInsn extends Comparable<JumpInsn> {

    JumpInsnNode getJumpInsnNode();

    int getIndexWithinBlock();

    int getIndexWithinMethod();

    Opcode getOpcode();

    boolean isAssignmentGuard();

}
