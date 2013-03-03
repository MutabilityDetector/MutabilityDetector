package de.htwg_konstanz.jia.lazyinitialisation;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;
import static org.objectweb.asm.Opcodes.DCMPG;
import static org.objectweb.asm.Opcodes.DCMPL;
import static org.objectweb.asm.Opcodes.FCMPG;
import static org.objectweb.asm.Opcodes.FCMPL;
import static org.objectweb.asm.Opcodes.IF_ACMPEQ;
import static org.objectweb.asm.Opcodes.IF_ACMPNE;
import static org.objectweb.asm.Opcodes.IF_ICMPEQ;
import static org.objectweb.asm.Opcodes.IF_ICMPGE;
import static org.objectweb.asm.Opcodes.IF_ICMPGT;
import static org.objectweb.asm.Opcodes.IF_ICMPLE;
import static org.objectweb.asm.Opcodes.IF_ICMPLT;
import static org.objectweb.asm.Opcodes.IF_ICMPNE;
import static org.objectweb.asm.Opcodes.LCMP;

import java.util.List;

import javax.annotation.concurrent.Immutable;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * 
 *
 * @author Juergen Fickel
 * @version 02.03.2013
 */
@Immutable
final class EffectiveJumpInstructionFinder {

    private final String variableName;
    private final ControlFlowBlock controlFlowBlock;

    private EffectiveJumpInstructionFinder(final String theVariableName, final ControlFlowBlock theControlFlowBlock) {
        variableName = theVariableName;
        controlFlowBlock = theControlFlowBlock;
    }

    public static EffectiveJumpInstructionFinder newInstance(final String variableName,
            final ControlFlowBlock controlFlowBlock) {
        return new EffectiveJumpInstructionFinder(notEmpty(variableName), notNull(controlFlowBlock));
    }


    /* Im aktuellen und in allen Vorgaengerbloecken:
     *     GETFIELD fuer `variableName` suchen.
     *     Lokale Variable fuer den Wert von `variableName` suchen (`?STORE x`).
     *     *Implementiert*
     * 
     * 
     * In Block mit Sprunganweisung:
     * Typ der Vorgaenger-Anweisung ermitteln:
     *     `?LOAD` x
     *         Alias suchen
     *         ? Stimmt x überein
     *             ? Bedingungspruefung entscheidet evtl. ueber Zuweisung
     *             : aktueller Block scheidet aus
     *         : `GETFIELD` fuer `variableName`
     *             ? relevantJumpInsns.put(jumpInsnWithIndex.getKey(), jumpInsnWithIndex.getValue());
     *             : mit naechster Sprunganweisung fortfahren.
     *         : Ist Anweisung n - 1 eine Vergleichsanweisung (z. B. `FCMPL`)
     *             ? Ist n - 2 `GETFIELD` fuer `variableName`
     *                 ? relevantJumpInsns.put(jumpInsnWithIndex.getKey(), jumpInsnWithIndex.getValue());
     *                 : mit naechster Sprunganweisung fortfahren.
     *                 *Implementiert*
     *
     *             : Ist n - 2 `?LOAD` fuer Alias x
     *                 ? relevantJumpInsns.put(jumpInsnWithIndex.getKey(), jumpInsnWithIndex.getValue());
     *                 : mit naechster Sprunganweisung fortfahren.
     *                 *Implementiert*
     *
     */
    public boolean isRelevantJumpInstruction(final int indexOfInstructionToAnalyse) {
        boolean result = false;
        final List<AbstractInsnNode> instructions = controlFlowBlock.getBlockInstructions();
        final int indexOfPredecessorInstruction = indexOfInstructionToAnalyse - 1;
        final AbstractInsnNode predecessorInstruction = instructions.get(indexOfPredecessorInstruction);
        if (isGetfieldForVariable(predecessorInstruction)) {
            result = true;
        } else if (isLoadInstructionForAlias(controlFlowBlock, predecessorInstruction)) {
            result = true;
        } else if (isComparisonInsn(predecessorInstruction)) {
            result = isRelevantJumpInstruction(indexOfPredecessorInstruction);
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

    private static boolean isComparisonInsn(final AbstractInsnNode abstractInsnNode) {
        switch (abstractInsnNode.getOpcode()) {
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

    private boolean isLoadInstructionForAlias(final ControlFlowBlock blockWithJumpInsn, final AbstractInsnNode insn) {
        final AliasFinder aliasFinder = AliasFinder.newInstance(variableName);
        final Alias alias = aliasFinder.searchForAliasInBlock(blockWithJumpInsn);
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

    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        b.append(getClass().getSimpleName()).append(" [");
        b.append("variableName=").append(variableName).append(", controlFlowBlock=").append(controlFlowBlock);
        b.append("]");
        return b.toString();
    }

}
