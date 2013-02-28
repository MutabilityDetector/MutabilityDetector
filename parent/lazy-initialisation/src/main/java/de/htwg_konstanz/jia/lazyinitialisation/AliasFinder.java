package de.htwg_konstanz.jia.lazyinitialisation;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.DSTORE;
import static org.objectweb.asm.Opcodes.FSTORE;
import static org.objectweb.asm.Opcodes.ISTORE;
import static org.objectweb.asm.Opcodes.LSTORE;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 27.02.2013
 */
@Immutable
final class AliasFinder {

    private final Set<ControlFlowBlock> alreadyVisited;
    private final String variableName;

    private AliasFinder(final String theVariableName) {
        alreadyVisited = new HashSet<ControlFlowBlock>();
        variableName = theVariableName;
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param variableName
     *            name of the instance variable to search aliases for. Must
     *            neither be {@code null} nor empty.
     * @return a new instance of this class.
     */
    public static AliasFinder newInstance(final String variableName) {
        return new AliasFinder(notEmpty(variableName));
    }

    /**
     * @param block
     *            a {@link ControlFlowBlock} which possibly contains the setup
     *            of an alias for a lazy variable. This method thereby examines
     *            predecessors of {@code block}, too. This parameter must not be
     *            {@code null}.
     * @return an {@link Alias}. This is never {@code null}. If {@code block}
     *         does not contain an alias, the following is being returned:
     *         {@code Alias [doesExist=false, localVariable=Integer.MIN_VALUE]}.
     */
    public Alias searchForAliasInBlock(final ControlFlowBlock block) {
        notNull(block);
        Alias result = Alias.newInstance(false, Integer.MIN_VALUE);
        if (alreadyVisited.contains(block)) {
            return result;
        }
        alreadyVisited.add(block);
        final List<AbstractInsnNode> insns = block.getBlockInstructions();
        final int indexOfGetfield = findIndexOfGetfieldForVariable(insns);
        if (indexOfGetfieldFound(indexOfGetfield)) {
            final AbstractInsnNode successorInsnOfGetfieldForVariable = insns.get(indexOfGetfield + 1);
            if (isStoreInstruction(successorInsnOfGetfieldForVariable)) {
                final VarInsnNode storeInstruction = (VarInsnNode) successorInsnOfGetfieldForVariable;
                result = Alias.newInstance(true, storeInstruction.var);
            }
        }
        if (!result.doesExist) {
            for (final ControlFlowBlock predecessor : block.getPredecessors()) {
                return searchForAliasInBlock(predecessor);
            }
        }
        return result;
    }

    private int findIndexOfGetfieldForVariable(final List<AbstractInsnNode> instructions) {
        int result = -1;
        int i = 0;
        for (final AbstractInsnNode instruction : instructions) {
            if (isGetfieldForVariable(instruction)) {
                result = i;
                break;
            }
            i++;
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

    private static boolean indexOfGetfieldFound(final int index) {
        return -1 < index;
    }

    private static boolean isStoreInstruction(final AbstractInsnNode insn) {
        final Set<Integer> storeInstructions = getStoreInstructions();
        final Integer opcode = Integer.valueOf(insn.getOpcode());
        return storeInstructions.contains(opcode);
    }

    private static Set<Integer> getStoreInstructions() {
        final IntegerSetBuilder b = IntegerSetBuilder.getInstance();
        b.add(ISTORE).add(LSTORE).add(FSTORE).add(DSTORE).add(ASTORE);
        return b.build();
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("AliasFinder [").append("variableName=").append(variableName);
        builder.append(", alreadyVisited=").append(alreadyVisited).append("]");
        return builder.toString();
    }

}
