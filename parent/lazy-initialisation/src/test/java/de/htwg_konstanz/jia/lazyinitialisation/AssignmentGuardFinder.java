/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import static org.junit.Assert.assertEquals;
import static org.objectweb.asm.Opcodes.*;

import java.util.*;
import java.util.Map.Entry;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import de.htwg_konstanz.jia.lazyinitialisation.ControlFlowBlock.ControlFlowBlockFactory;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.AliasedByteWithDefault;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.AliasedFloatWithDefault;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.IntegerWithDefault;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 20.02.2013
 */
public final class AssignmentGuardFinder {

    @NotThreadSafe
    private static final class IntegerSetBuilder {
        private final Set<Integer> resultSet;

        private IntegerSetBuilder() {
            resultSet = new HashSet<Integer>();
        }

        public static IntegerSetBuilder getInstance() {
            return new IntegerSetBuilder();
        }

        public IntegerSetBuilder add(final int integer) {
            resultSet.add(Integer.valueOf(integer));
            return this;
        }

        public Set<Integer> build() {
            return resultSet;
        }
    } // class IntegerSetBuilder


    @Immutable
    private static final class Alias {
        public final boolean doesExist;
        public final int localVariable;

        private Alias(final boolean doesExist, final int localVariable) {
            this.doesExist = doesExist;
            this.localVariable = localVariable;
        }

        public static Alias newInstance(final boolean doesExist, final int localVariable) {
            return new Alias(doesExist, localVariable);
        }

        @Override
        public String toString() {
            final StringBuilder b = new StringBuilder();
            b.append("Alias [").append("doesExist=").append(doesExist);
            b.append(", localVariable=").append(localVariable).append("]");
            return b.toString();
        }
    } // class Alias


    private final class AliasFinder {

        public Alias searchForAliasInBlock(final ControlFlowBlock block) {
            Alias result = Alias.newInstance(false, Integer.MIN_VALUE);
            final AbstractInsnNode[] insns = toArray(block.getInstructions());
            int indexOfGetfield = findIndexOfGetfieldForVariable(insns);
            if (indexOfGetfieldFound(indexOfGetfield)) {
                final AbstractInsnNode successorInsnOfGetfieldForVariable = insns[indexOfGetfield + 1];
                if (isStoreInstruction(successorInsnOfGetfieldForVariable)) {
                    final VarInsnNode storeInsn = (VarInsnNode) successorInsnOfGetfieldForVariable;
                    result = Alias.newInstance(true, storeInsn.var);
                }
            }
            if (!result.doesExist) {
                for (final ControlFlowBlock predecessor : block.getPredecessors()) {
                    return searchForAliasInBlock(predecessor);
                }
            }
            return result;
        }

        private AbstractInsnNode[] toArray(final List<AbstractInsnNode> asList) {
            final List<AbstractInsnNode> instructions = asList;
            return instructions.toArray(new AbstractInsnNode[asList.size()]);
        }

        private int findIndexOfGetfieldForVariable(final AbstractInsnNode[] instructions) {
            int result = -1;
            for (int i = 0; i < instructions.length; i++) {
                if (isGetfieldForVariable(instructions[i])) {
                    result = i;
                    break;
                }
            }
            return result;
        }

        private boolean indexOfGetfieldFound(final int index) {
            return -1 < index;
        }

        private boolean isStoreInstruction(final AbstractInsnNode insn) {
            final Set<Integer> storeInstructions = getStoreInstructions();
            final Integer opcode = Integer.valueOf(insn.getOpcode());
            return storeInstructions.contains(opcode);
        }

        private Set<Integer> getStoreInstructions() {
            final IntegerSetBuilder b = IntegerSetBuilder.getInstance();
            b.add(ISTORE).add(LSTORE).add(FSTORE).add(DSTORE).add(ASTORE);
            return b.build();
        }

    } // class AliasFinder


    private Map<Integer, ControlFlowBlock> controlFlowBlocks = null;
    private String variableName = null;
    private Map<Integer, JumpInsnNode> relevantJumpInsns = null;

    @After
    public void tearDown() {
        controlFlowBlocks = null;
        variableName = null;
        relevantJumpInsns = null;
    }

    @Before
    public void setUp() {
        relevantJumpInsns = new HashMap<Integer, JumpInsnNode>();
    }

    @Test
    public void findAssignmentGuardForIntegerWithDefault() {
        analyseJumpInstructionsFor(IntegerWithDefault.class, "hash", "hashCode", 0);
        assertEquals(1, relevantJumpInsns.size());
        for (final Entry<Integer, JumpInsnNode> entry : relevantJumpInsns.entrySet()) {
            assertEquals(Integer.valueOf(4), entry.getKey());
        }
    }

    private void analyseJumpInstructionsFor(final Class<?> klasse, final String theVariableName,
            final String methodName, final int blockNumber) {
        initialiseAll(klasse, theVariableName, methodName);
        final ControlFlowBlock blockWithJumpInsn = controlFlowBlocks.get(Integer.valueOf(blockNumber));
        analyseJumpInstructions(getAllJumpInsructionsOfBlock(blockWithJumpInsn), blockWithJumpInsn);
    }

    private void initialiseAll(final Class<?> klasse, final String theVariableName, final String methodName) {
        variableName = theVariableName;
        controlFlowBlocks = initialiseControlFlowBlocksFor(klasse, methodName);
    }

    private static Map<Integer, ControlFlowBlock> initialiseControlFlowBlocksFor(final Class<?> klasse,
            final String methodName) {
        final ClassNodeFactory factory = ClassNodeFactory.getInstance();
        final ConvenienceClassNode ccn = factory.convenienceClassNodeFor(klasse);
        final MethodNode methodNode = ccn.findMethodWithName(methodName);
        if (null != methodNode) {
            final ControlFlowBlockFactory cfbFactory = ControlFlowBlockFactory.newInstance(ccn.name(), methodNode);
            return cfbFactory.getAllControlFlowBlocksForMethodInMap();
        }
        return Collections.emptyMap();
    }

    private static Map<Integer, JumpInsnNode> getAllJumpInsructionsOfBlock(final ControlFlowBlock blockWithJumpInsn) {
        final List<AbstractInsnNode> instructions = blockWithJumpInsn.getInstructions();
        final Map<Integer, JumpInsnNode> result = new HashMap<Integer, JumpInsnNode>();
        for (final AbstractInsnNode abstractInsnNode : instructions) {
            if (isJumpInsnNode(abstractInsnNode)) {
                result.put(instructions.indexOf(abstractInsnNode), (JumpInsnNode) abstractInsnNode);
            }
        }
        return Collections.unmodifiableMap(result);
    }

    private static boolean isJumpInsnNode(final AbstractInsnNode abstractInsnNode) {
        return AbstractInsnNode.JUMP_INSN == abstractInsnNode.getType();
    }

    private void analyseJumpInstructions(final Map<Integer, JumpInsnNode> jumpInsnNodes,
            final ControlFlowBlock blockWithJumpInsn) {
        for (final Entry<Integer, JumpInsnNode> jumpInsnWithIndex : jumpInsnNodes.entrySet()) {
            if (isRelevantJumpInstruction(jumpInsnWithIndex.getKey(), blockWithJumpInsn)) {
                addToRelevantJumpInsns(jumpInsnWithIndex);
            }
        }
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
    private boolean isRelevantJumpInstruction(final int indexOfInstructionToAnalyse,
            final ControlFlowBlock blockWithJumpInsn) {
        boolean result = false;
        final List<AbstractInsnNode> instructions = blockWithJumpInsn.getInstructions();
        final int indexOfPredecessorInstruction = indexOfInstructionToAnalyse - 1;
        final AbstractInsnNode predecessorInstruction = instructions.get(indexOfPredecessorInstruction);
        if (isGetfieldForVariable(predecessorInstruction)) {
            result = true;
        } else if (isLoadInstructionForAlias(blockWithJumpInsn, predecessorInstruction)) {
            result = true;
        } else if (isComparisonInsn(predecessorInstruction)) {
            result = isRelevantJumpInstruction(indexOfPredecessorInstruction, blockWithJumpInsn);
        }
        return result;
    }

// TODO Löschen (Implementierung ohne Rekursion)
//    private void isRelevantJumpInstruction(final Entry<Integer, JumpInsnNode> jumpInsnWithIndex,
//            final ControlFlowBlock blockWithJumpInsn) {
//        final List<AbstractInsnNode> instructions = blockWithJumpInsn.getInstructions();
//        final int indexOfPredecessorInstruction = jumpInsnWithIndex.getKey() - 1;
//        final AbstractInsnNode predecessorInstruction = instructions.get(indexOfPredecessorInstruction);
//        if (isGetfieldForVariable(predecessorInstruction)) {
//            addToRelevantJumpInsns(jumpInsnWithIndex);
//        } else if (isLoadInstructionForAlias(blockWithJumpInsn, predecessorInstruction)) {
//            addToRelevantJumpInsns(jumpInsnWithIndex);
//        } else {
//            if (isComparisonInsn(predecessorInstruction)) {
//            final AbstractInsnNode prePredecessorInstruction = instructions.get(indexOfPredecessorInstruction - 1);
//            isParticularJumpInstruction(jumpInsnWithIndex, blockWithJumpInsn);
//            if (isGetfieldForVariable(prePredecessorInstruction)) {
//                addToRelevantJumpInsns(jumpInsnWithIndex);
//            } else if (isLoadInstructionForAlias(blockWithJumpInsn, prePredecessorInstruction)) {
//                addToRelevantJumpInsns(jumpInsnWithIndex);
//            }
//            }
//        }
//    }

    private boolean isGetfieldForVariable(final AbstractInsnNode insn) {
        boolean result = false;
        if (Opcodes.GETFIELD == insn.getOpcode()) {
            final FieldInsnNode getfield = (FieldInsnNode) insn;
            result = variableName.equals(getfield.name);
        }
        return result;
    }

    private void addToRelevantJumpInsns(final Entry<Integer, JumpInsnNode> entryToAdd) {
        relevantJumpInsns.put(entryToAdd.getKey(), entryToAdd.getValue());
    }

    private boolean isComparisonInsn(final AbstractInsnNode abstractInsnNode) {
        final Set<Integer> compareInstructions = getComparisonInstructions();
        final Integer opcode = Integer.valueOf(abstractInsnNode.getOpcode());
        return compareInstructions.contains(opcode);    
    }

    private static Set<Integer> getComparisonInstructions() {
        final IntegerSetBuilder b = IntegerSetBuilder.getInstance();
        b.add(LCMP).add(FCMPL).add(FCMPG).add(DCMPL).add(DCMPG);
        b.add(IF_ICMPEQ).add(IF_ICMPNE).add(IF_ICMPLT).add(IF_ICMPGE).add(IF_ICMPGT).add(IF_ICMPLE);
        b.add(IF_ACMPEQ).add(IF_ACMPNE);
        return b.build();
    }
    
    private boolean isLoadInstructionForAlias(final ControlFlowBlock blockWithJumpInsn, final AbstractInsnNode insn) {
        final AliasFinder aliasFinder = new AliasFinder();
        final Alias alias = aliasFinder.searchForAliasInBlock(blockWithJumpInsn);
        return alias.doesExist && isLoadInstructionForAlias(insn, alias);
    }

    private boolean isLoadInstructionForAlias(final AbstractInsnNode insn, final Alias alias) {
        boolean result = false;
        if (AbstractInsnNode.VAR_INSN == insn.getType()) {
            final VarInsnNode loadInstruction = (VarInsnNode) insn;
            result = loadInstruction.var == alias.localVariable;
        }
        return result;
    }

    @Test
    public void findAssignmentGuardForAliasedFloatWithDefault() {
        analyseJumpInstructionsFor(AliasedFloatWithDefault.class, "hash", "hashCodeFloat", 1);
        assertEquals(1, relevantJumpInsns.size());
    }

    @Test
    public void findAssignmentGuardForAliasedByteWithDefault() {
        analyseJumpInstructionsFor(AliasedByteWithDefault.class, "hash", "hashCodeByte", 1);
        assertEquals(1, relevantJumpInsns.size());
    }

    @Test
    public void findAssignmentGuardForJavaLangString() {
        analyseJumpInstructionsFor(String.class, "hash", "hashCode", 0);
        assertEquals(1, relevantJumpInsns.size());
    }

}
