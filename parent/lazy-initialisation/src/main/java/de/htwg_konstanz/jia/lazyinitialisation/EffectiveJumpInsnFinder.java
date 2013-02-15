/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 15.02.2013
 */
final class EffectiveJumpInsnFinder {

//    private final AssignmentInsn effectivePutfieldInsn;
    
    public Set<JumpInsn> getIndexOfEffectiveJumpInsn(final AssignmentInsn effectivePutfieldInsn, final InsnList instructions) {
        final Set<JumpInsn> result = new HashSet<JumpInsn>();
        final AbstractInsnNode[] insns = instructions.toArray();
        for (int i = 0; i < insns.length; i++) {
            final AbstractInsnNode abstractInsnNode = insns[i];
            if (isJumpInsn(abstractInsnNode)) {
                final JumpInsnNode jumpInsn = (JumpInsnNode) abstractInsnNode;
                if (effectivePutfieldInsn.isUnderLabel(jumpInsn.label)) {
                    jumpInsnsToEffectivePutfieldInsn.put(Integer.valueOf(i), jumpInsn);
                }
            }
        }
        return result;
    }

    private static boolean isJumpInsn(final AbstractInsnNode abstractInsnNode) {
        return AbstractInsnNode.JUMP_INSN == abstractInsnNode.getType();
    }

}
