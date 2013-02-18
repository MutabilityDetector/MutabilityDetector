/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;

import de.htwg_konstanz.jia.lazyinitialisation.ControlFlowBlock.ControlFlowBlockFactory;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 15.02.2013
 */
@Immutable
final class EffectiveJumpInsnFinder {

    private final AssignmentInsn effectivePutfieldInsn;
    private final AbstractInsnNode[] instructions;
    private final List<JumpInsn> associatedJumpInstructions;

    private EffectiveJumpInsnFinder(final AssignmentInsn theEffectivePutfieldInsn, final InsnList theInstructions) {
        effectivePutfieldInsn = theEffectivePutfieldInsn;
        final AbstractInsnNode[] originalInstructions = theInstructions.toArray();
        instructions = Arrays.copyOf(originalInstructions, originalInstructions.length);
        associatedJumpInstructions = new ArrayList<JumpInsn>();
    }

    public static EffectiveJumpInsnFinder newInstance(final AssignmentInsn effectivePutfieldInsn,
            final InsnList instructions) {
        final EffectiveJumpInsnFinder result = new EffectiveJumpInsnFinder(notNull(effectivePutfieldInsn),
                notNull(instructions));
        result.collectAndSortAssociatedJumpInstructions();
        return result;
    }

    private void collectAndSortAssociatedJumpInstructions() {
        for (int i = 0; i < instructions.length; i++) {
            final AbstractInsnNode abstractInsnNode = instructions[i];
            addIfAssociatedJumpInstruction(i, abstractInsnNode);
        }
        Collections.sort(associatedJumpInstructions);
    }

    private void addIfAssociatedJumpInstruction(final int i, final AbstractInsnNode abstractInsnNode) {
        if (isJumpInsn(abstractInsnNode)) {
            final JumpInsnNode jumpInsn = (JumpInsnNode) abstractInsnNode;
            if (effectivePutfieldInsn.isUnderLabel(jumpInsn.label)) {
                associatedJumpInstructions.add(DefaultJumpInsn.newInstance(jumpInsn, i, effectivePutfieldInsn));
            }
        }
    }

    private static boolean isJumpInsn(final AbstractInsnNode abstractInsnNode) {
        return AbstractInsnNode.JUMP_INSN == abstractInsnNode.getType();
    }
    
//    public JumpInsn getEffectiveJumpInsn() {
//        final int indexOfEffectiveJumpInstruction = associatedJumpInstructions.size() - 1;
//        return associatedJumpInstructions.get(indexOfEffectiveJumpInstruction);
//    }

    public JumpInsn getEffectiveJumpInsn() {
        final int indexOfEffectiveJumpInstruction = associatedJumpInstructions.size() - 1;
        return associatedJumpInstructions.get(indexOfEffectiveJumpInstruction);
    }

    /**
     * @return {@code true} iff there are more than one jump instructions which
     *         lead to the effective assignment instruction.
     */
    public boolean hasMoreThanOneAssociatedJumpInstruction() {
        return 1 < associatedJumpInstructions.size();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + associatedJumpInstructions.hashCode();
        result = prime * result + effectivePutfieldInsn.hashCode();
        result = prime * result + Arrays.hashCode(instructions);
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
        if (!(obj instanceof EffectiveJumpInsnFinder)) {
            return false;
        }
        final EffectiveJumpInsnFinder other = (EffectiveJumpInsnFinder) obj;
        if (!associatedJumpInstructions.equals(other.associatedJumpInstructions)) {
            return false;
        }
        if (!effectivePutfieldInsn.equals(other.effectivePutfieldInsn)) {
            return false;
        }
        if (!Arrays.equals(instructions, other.instructions)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("effectivePutfieldInsn", effectivePutfieldInsn).append("instructions", instructions);
        builder.append("associatedJumpInstructions", associatedJumpInstructions);
        return builder.toString();
    }

}
