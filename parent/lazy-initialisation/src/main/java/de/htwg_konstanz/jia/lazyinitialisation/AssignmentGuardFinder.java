package de.htwg_konstanz.jia.lazyinitialisation;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;
import static org.objectweb.asm.Opcodes.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
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

    private final String variableName;
    private final ControlFlowBlock controlFlowBlock;

    private AssignmentGuardFinder(final String theVariableName, final ControlFlowBlock theControlFlowBlock) {
        variableName = theVariableName;
        controlFlowBlock = theControlFlowBlock;
    }

    public static AssignmentGuardFinder newInstance(final String variableName,
            final ControlFlowBlock controlFlowBlock) {
        return new AssignmentGuardFinder(notEmpty(variableName), notNull(controlFlowBlock));
    }

    @Override
    public JumpInsn find() {
        final Set<JumpInsn> supposedAssignmentGuards = collectSupposedAssignmentGuards();
        JumpInsn result = NullJumpInsn.getInstance();
        if (1 < supposedAssignmentGuards.size()) {
            throw new IllegalStateException("There exists more than one assignment guard in this block.");
        }
        for (final JumpInsn jumpInsn : supposedAssignmentGuards) {
            result = jumpInsn;
            break;
        }
        return result;
    }

    private Set<JumpInsn> collectSupposedAssignmentGuards() {
        final Set<JumpInsn> result = new HashSet<JumpInsn>();
        for (final JumpInsn jumpInsn : controlFlowBlock.getJumpInstructions()) {
            final AssignmentGuard.Builder builder = new AssignmentGuard.Builder(jumpInsn);
            final JumpInsn possibleAssignmentGuard = getAssignmentGuard(jumpInsn.getIndexWithinBlock(), builder);
            if (possibleAssignmentGuard.isAssignmentGuard()) {
                result.add(possibleAssignmentGuard);
            }
        }
        return result;
    }

    private JumpInsn getAssignmentGuard(final int indexOfInstructionToAnalyse, final AssignmentGuard.Builder builder) {
        final JumpInsn result;
        final List<AbstractInsnNode> blockInstructions = controlFlowBlock.getBlockInstructions();
        final int indexOfPredecessorInstruction = indexOfInstructionToAnalyse - 1;
        final AbstractInsnNode predecessorInstruction = blockInstructions.get(indexOfPredecessorInstruction);
        builder.addPredecessorInstruction(predecessorInstruction);
        if (isGetfieldForVariable(predecessorInstruction)) {
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

    boolean isAssignmentGuard(final int indexOfInstructionToAnalyse) {
        boolean result = false;
        final List<AbstractInsnNode> blockInstructions = controlFlowBlock.getBlockInstructions();
        final int indexOfPredecessorInstruction = indexOfInstructionToAnalyse - 1;
        final AbstractInsnNode predecessorInstruction = blockInstructions.get(indexOfPredecessorInstruction);
        if (isGetfieldForVariable(predecessorInstruction)) {
            result = true;
        } else if (isLoadInstructionForAlias(predecessorInstruction)) {
            result = true;
        } else if (isEqualsInstruction(predecessorInstruction)) {
            result = false;
        } else if (isPushNullOntoStackInstruction(predecessorInstruction)) {
            result = isAssignmentGuard(indexOfPredecessorInstruction);
        } else if (isComparisonInstruction(predecessorInstruction)) {
            result = isAssignmentGuard(indexOfPredecessorInstruction);
        }
        return result;
    }

    private boolean isGetfieldForVariable(final AbstractInsnNode insn) {
        boolean result = false;
        if (Opcodes.GETFIELD == insn.getOpcode()) {
            final FieldInsnNode getfield = (FieldInsnNode) insn;
            result = variableName.equals(getfield.name);
        }
        return result;
    }

    private boolean isLoadInstructionForAlias(final AbstractInsnNode insn) {
        final Finder<Alias> f = AliasFinder.newInstance(variableName, controlFlowBlock);
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
        b.append(getClass().getSimpleName()).append(" [variableName=").append(variableName);
        b.append(", controlFlowBlock=").append(controlFlowBlock).append("]");
        return b.toString();
    }

}
