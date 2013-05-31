package org.mutabilitydetector.checkers.settermethod;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;
import static org.objectweb.asm.Opcodes.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * 
 * 
 * @author Juergen Fickel
 * @version 02.03.2013
 */
@Immutable
final class AssignmentGuardFinder implements Finder<JumpInsn> {

    private final String candidateName;
    private final ControlFlowBlock controlFlowBlock;

    private AssignmentGuardFinder(final String theCandidateName, final ControlFlowBlock theControlFlowBlock) {
        candidateName = theCandidateName;
        controlFlowBlock = theControlFlowBlock;
    }

    /**
     * Creates a new instance of this class. None of the arguments must be
     * {@code null}.
     * 
     * @param candidateName
     *            name of the lazy variable. Must not be empty!
     * @param controlFlowBlock
     *            the control flow block which is supposed to contain an
     *            {@link AssignmentGuard}.
     * @return a new instance of this class.
     */
    public static AssignmentGuardFinder newInstance(final String candidateName,
            final ControlFlowBlock controlFlowBlock) {
        return new AssignmentGuardFinder(notEmpty(candidateName), notNull(controlFlowBlock));
    }

    @Override
    public JumpInsn find() {
        final Collection<JumpInsn> supposedAssignmentGuards = collectSupposedAssignmentGuards();
        if (1 < supposedAssignmentGuards.size()) {
            throw new IllegalStateException("There exists more than one assignment guard in this block.");
        }
        for (final JumpInsn jumpInsn : supposedAssignmentGuards) {
            return jumpInsn;
        }
        return NullJumpInsn.getInstance();
    }

    private Collection<JumpInsn> collectSupposedAssignmentGuards() {
        final Set<JumpInsn> result = new HashSet<JumpInsn>();
        for (final JumpInsn jumpInsn : collectAllConditionCheckInstructionsOfBlock()) {
            final AssignmentGuard.Builder builder = new AssignmentGuard.Builder(jumpInsn);
            final JumpInsn possibleAssignmentGuard = getAssignmentGuard(jumpInsn.getIndexWithinBlock(), builder);
            if (possibleAssignmentGuard.isAssignmentGuard()) {
                result.add(possibleAssignmentGuard);
            }
        }
        return result;
    }

    private Collection<JumpInsn> collectAllConditionCheckInstructionsOfBlock() {
        final ArrayList<JumpInsn> result = new ArrayList<JumpInsn>();
        int indexWithinBlock = 0;
        for (final AbstractInsnNode insn : controlFlowBlock.getBlockInstructions()) {
            if (isConditionCheckInstruction(insn)) {
                final JumpInsnNode jumpInsnNode = (JumpInsnNode) insn;
                final int indexWithinMethod = controlFlowBlock.getIndexWithinMethod(indexWithinBlock);
                result.add(DefaultJumpInsn.newInstance(jumpInsnNode, indexWithinBlock, indexWithinMethod));
            }
            indexWithinBlock++;
        }
        result.trimToSize();
        return result;
    }

    private static boolean isConditionCheckInstruction(final AbstractInsnNode insn) {
        final int opcode = insn.getOpcode();
        return AbstractInsnNode.JUMP_INSN == insn.getType() && opcode != Opcodes.GOTO && opcode != Opcodes.JSR
                && opcode != Opcodes.RET;
    }

    private JumpInsn getAssignmentGuard(final int indexOfInstructionToAnalyse, final AssignmentGuard.Builder builder) {
        final JumpInsn result;
        final List<AbstractInsnNode> blockInstructions = controlFlowBlock.getBlockInstructions();
        final int indexOfPredecessorInstruction = indexOfInstructionToAnalyse - 1;
        final AbstractInsnNode predecessorInstruction = blockInstructions.get(indexOfPredecessorInstruction);
        builder.addPredecessorInstruction(predecessorInstruction);
        if (isGetfieldOrGetstaticForCandidate(predecessorInstruction)) {
            result = builder.build();
        } else if (isLoadInstructionForAlias(predecessorInstruction)) {
            result = builder.build();
        } else if (isEqualsInstruction(predecessorInstruction)) {
            result = NullJumpInsn.getInstance();
        } else if (isPushNullOntoStackInstruction(predecessorInstruction)) {
            result = getAssignmentGuard(indexOfPredecessorInstruction, builder);
        } else if (isComparisonInstruction(predecessorInstruction)) {
            result = getAssignmentGuard(indexOfPredecessorInstruction, builder);
        } else {
            result = NullJumpInsn.getInstance();
        }
        return result;
    }

    private boolean isGetfieldOrGetstaticForCandidate(final AbstractInsnNode insn) {
        boolean result = false;
        if (GETFIELD == insn.getOpcode() || GETSTATIC == insn.getOpcode()) {
            final FieldInsnNode getInstruction = (FieldInsnNode) insn;
            result = candidateName.equals(getInstruction.name);
        }
        return result;
    }

    private boolean isLoadInstructionForAlias(final AbstractInsnNode insn) {
        final Finder<Alias> f = AliasFinder.newInstance(candidateName, controlFlowBlock);
        final Alias alias = f.find();
        return alias.doesExist && isLoadInstructionForAlias(insn, alias);
    }

    private static boolean isLoadInstructionForAlias(final AbstractInsnNode insn, final Alias alias) {
        boolean result = false;
        if (AbstractInsnNode.VAR_INSN == insn.getType()) {
            final VarInsnNode loadInstruction = (VarInsnNode) insn;
            result = loadInstruction.var == alias.localVariable;
        }
        return result;
    }

    private static boolean isEqualsInstruction(final AbstractInsnNode insn) {
        final boolean result;
        if (AbstractInsnNode.METHOD_INSN == insn.getType()) {
            final MethodInsnNode methodInsnNode = (MethodInsnNode) insn;
            result = methodInsnNode.name.equals("equals");
        } else {
            result = false;
        }
        return result;
    }

    private static boolean isPushNullOntoStackInstruction(final AbstractInsnNode insn) {
        return ACONST_NULL == insn.getOpcode();
    }

    private static boolean isComparisonInstruction(final AbstractInsnNode insn) {
        switch (insn.getOpcode()) {
        case LCMP:
        case FCMPL:
        case FCMPG:
        case DCMPL:
        case DCMPG:
        case IF_ICMPEQ:
        case IF_ICMPNE:
        case IF_ICMPLT:
        case IF_ICMPGE:
        case IF_ICMPGT:
        case IF_ICMPLE:
        case IF_ACMPEQ:
        case IF_ACMPNE:
            return true;
        default:
            return false;
        }
    }

    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        b.append(getClass().getSimpleName()).append(" [candidateName=").append(candidateName);
        b.append(", controlFlowBlock=").append(controlFlowBlock).append("]");
        return b.toString();
    }

}
