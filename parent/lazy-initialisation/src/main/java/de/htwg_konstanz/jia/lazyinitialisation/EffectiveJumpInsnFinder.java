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
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 15.02.2013
 */
@Immutable
final class EffectiveJumpInsnFinder {

    private final AssignmentInsn effectiveAssignmentInsn;
    private final AbstractInsnNode[] instructions;
    private final List<JumpInsn> associatedJumpInstructions;

    private EffectiveJumpInsnFinder(final AssignmentInsn theEffectiveAssignmentInsn,
            final AbstractInsnNode[] theInstructions) {
        effectiveAssignmentInsn = theEffectiveAssignmentInsn;
        instructions = Arrays.copyOf(theInstructions, theInstructions.length);
        associatedJumpInstructions = new ArrayList<JumpInsn>();
    }

    public static EffectiveJumpInsnFinder newInstance(final AssignmentInsn effectiveAssignmentInsn,
            final InsnList instructions) {
        return newInstance(effectiveAssignmentInsn, instructions.toArray());
    }

    public static EffectiveJumpInsnFinder newInstance(final AssignmentInsn effectiveAssignmentInsn,
            final AbstractInsnNode[] instructions) {
        final EffectiveJumpInsnFinder result = new EffectiveJumpInsnFinder(notNull(effectiveAssignmentInsn),
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

//    private void addIfAssociatedJumpInstruction(final int i, final AbstractInsnNode abstractInsnNode) {
//        if (isJumpInsn(abstractInsnNode)) {
//            final JumpInsnNode jumpInsn = (JumpInsnNode) abstractInsnNode;
//            if (effectiveAssignmentInsn.isUnderLabel(jumpInsn.label)) {
//                associatedJumpInstructions.add(DefaultJumpInsn.newInstance(jumpInsn, i, effectiveAssignmentInsn));
//            }
//        }
//    }

    private void addIfAssociatedJumpInstruction(final int i, final AbstractInsnNode abstractInsnNode) {
        if (isJumpInsn(abstractInsnNode)) {
            final JumpInsnNode jumpInsn = (JumpInsnNode) abstractInsnNode;
            final int indexOfPredecessor = i - 1;
            if (0 < indexOfPredecessor) {
                final AbstractInsnNode predecessor = instructions[indexOfPredecessor];
                addIfPredecessorIsEffectiveAssignmentInsnForLazyVariable(predecessor, i, jumpInsn);
            }
        }
    }

    private void addIfPredecessorIsEffectiveAssignmentInsnForLazyVariable(final AbstractInsnNode predecessor,
            final int i,
            final JumpInsnNode jumpInsn) {
        if (null != predecessor && isGetfieldInstructionForLazyVariable(predecessor)) {
            associatedJumpInstructions.add(DefaultJumpInsn.newInstance(jumpInsn, i, effectiveAssignmentInsn));
        }
    }

    private static boolean isJumpInsn(final AbstractInsnNode abstractInsnNode) {
        return AbstractInsnNode.JUMP_INSN == abstractInsnNode.getType();
    }

    private boolean isGetfieldInstructionForLazyVariable(final AbstractInsnNode insn) {
        boolean result = false;
        if (Opcodes.GETFIELD == insn.getOpcode()) {
            final FieldInsnNode getfieldInsn = (FieldInsnNode) insn;
            final String nameOfAssignedVariable = getfieldInsn.name;
            result = nameOfAssignedVariable.equals(effectiveAssignmentInsn.getNameOfAssignedVariable());
        }
        return result;
    }

    public JumpInsn getEffectiveJumpInsn() {
        final int indexOfEffectiveJumpInstruction = associatedJumpInstructions.size() - 1;
        if (associatedJumpInstructions.isEmpty()) {
            return NullJumpInsn.getInstance();
        }
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
        result = prime * result + effectiveAssignmentInsn.hashCode();
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
        if (!effectiveAssignmentInsn.equals(other.effectiveAssignmentInsn)) {
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
        builder.append("effectivePutfieldInsn", effectiveAssignmentInsn).append("instructions", instructions);
        builder.append("associatedJumpInstructions", associatedJumpInstructions);
        return builder.toString();
    }

}
