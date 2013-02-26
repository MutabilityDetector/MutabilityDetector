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
import de.htwg_konstanz.jia.lazyinitialisation.VariableSetterCollection.Setters;
import de.htwg_konstanz.jia.lazyinitialisation.doublecheck.AliasedIntegerWithDefault;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.*;

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
    private static class BooleanIntegerPair {
        public final boolean doesExist;
        public final int localVariable;

        private BooleanIntegerPair(final boolean doesExist, final int localVariable) {
            this.doesExist = doesExist;
            this.localVariable = localVariable;
        }

        public static BooleanIntegerPair newInstance(final boolean doesExist, final int localVariable) {
            return new BooleanIntegerPair(doesExist, localVariable);
        }

        @Override
        public String toString() {
            final StringBuilder b = new StringBuilder();
            b.append("BooleanIntegerPair [").append("doesExist=").append(doesExist);
            b.append(", localVariable=").append(localVariable).append("]");
            return b.toString();
        }
    } // class Alias


    @Immutable
    private static final class Alias extends BooleanIntegerPair {

        private Alias(final boolean doesExist, final int localVariable) {
            super(doesExist, localVariable);
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

        private final Set<ControlFlowBlock> alreadyVisited = new HashSet<ControlFlowBlock>();

        public Alias searchForAliasInBlock(final ControlFlowBlock block) {
            Alias result = Alias.newInstance(false, Integer.MIN_VALUE);
            if (!alreadyVisited.contains(block)) {
                alreadyVisited.add(block);
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
    private Set<UnknownTypeValue> possibleInitialValuesForVariable = null;
    private Map<Integer, Map<Integer, JumpInsnNode>> relevantJumpInsns = null;

    @After
    public void tearDown() {
        controlFlowBlocks = null;
        variableName = null;
        possibleInitialValuesForVariable = null;
        relevantJumpInsns = null;
    }

    @Before
    public void setUp() {
        relevantJumpInsns = new HashMap<Integer, Map<Integer, JumpInsnNode>>();
    }

    @Test
    public void findAssignmentGuardForIntegerWithDefault() {
        analyseJumpInstructionsFor(IntegerWithDefault.class, "hash", "hashCode", 0);
        assertEquals(1, relevantJumpInsns.size());

        // TODO Neu: Versuch
        final Map<Integer, JumpInsnNode> indexedJumpInsnsOfBlock = relevantJumpInsns.get(0);
        for (final Entry<Integer, JumpInsnNode> entry : indexedJumpInsnsOfBlock.entrySet()) {
            assertEquals(Integer.valueOf(4), entry.getKey());
        }
//        for (final Entry<Integer, JumpInsnNode> entry : relevantJumpInsns.entrySet()) {
//            assertEquals(Integer.valueOf(4), entry.getKey());
//        }
        foo();
    }

    private void analyseJumpInstructionsFor(final Class<?> klasse, final String theVariableName,
            final String methodName, final int blockNumber) {
        initialiseAll(klasse, theVariableName, methodName);
        final ControlFlowBlock blockWithJumpInsn = controlFlowBlocks.get(Integer.valueOf(blockNumber));
        collectRelevantJumpInstructions(getAllJumpInsructionsOfBlock(blockWithJumpInsn), blockWithJumpInsn);
    }

    private void initialiseAll(final Class<?> klasse, final String theVariableName, final String methodName) {
        variableName = theVariableName;
        final ClassNodeFactory factory = ClassNodeFactory.getInstance();
        final ConvenienceClassNode ccn = factory.convenienceClassNodeFor(klasse);
        controlFlowBlocks = initialiseControlFlowBlocksFor(ccn, methodName);
        possibleInitialValuesForVariable = initialisePossibleInitialValuesFor(ccn, theVariableName);
    }

    private static Map<Integer, ControlFlowBlock> initialiseControlFlowBlocksFor(final ConvenienceClassNode ccn,
            final String methodName) {
        final MethodNode methodNode = ccn.findMethodWithName(methodName);
        if (null != methodNode) {
            final ControlFlowBlockFactory cfbFactory = ControlFlowBlockFactory.newInstance(ccn.name(), methodNode);
            return cfbFactory.getAllControlFlowBlocksForMethodInMap();
        }
        return Collections.emptyMap();
    }

    private static Set<UnknownTypeValue> initialisePossibleInitialValuesFor(final ConvenienceClassNode ccn,
            final String variableName) {
        Set<UnknownTypeValue> result = Collections.emptySet();
        final VariableSetterCollection variableSetterCollection = ccn.getVariableSetterCollection();
        final FieldNode variable = ccn.findVariableWithName(variableName);
        if (null != variable) {
            final Setters setters = variableSetterCollection.getSettersFor(variable);
            final InitialValueFinder initialValueFinder = InitialValueFinder.newInstance(variable, setters);
            initialValueFinder.run();
            result = initialValueFinder.getPossibleInitialValues();
        }
        return result;
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

    private void collectRelevantJumpInstructions(final Map<Integer, JumpInsnNode> jumpInsnNodes,
            final ControlFlowBlock blockWithJumpInsn) {
        for (final Entry<Integer, JumpInsnNode> jumpInsnWithIndex : jumpInsnNodes.entrySet()) {
            if (isRelevantJumpInstruction(jumpInsnWithIndex.getKey(), blockWithJumpInsn)) {
                // TODO Neu: Versuch
                final Map<Integer, JumpInsnNode> indexedJumpInsnsOfBlock = new HashMap<Integer, JumpInsnNode>();
                indexedJumpInsnsOfBlock.put(jumpInsnWithIndex.getKey(), jumpInsnWithIndex.getValue());
                relevantJumpInsns.put(blockWithJumpInsn.getBlockNumber(), indexedJumpInsnsOfBlock);
//                addToRelevantJumpInsns(jumpInsnWithIndex);
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
            /*
             * 
             */
            result = isRelevantJumpInstruction(indexOfPredecessorInstruction, blockWithJumpInsn);
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
    public void findAssignmentGuardForFloatWithDefault() {
        analyseJumpInstructionsFor(FloatWithDefault.class, "hash", "hashCodeFloat", 0);
        assertEquals(1, relevantJumpInsns.size());
        foo();
    }

    @Test
    public void findAssignmentGuardForAliasedFloatWithDefault() {
        analyseJumpInstructionsFor(AliasedFloatWithDefault.class, "hash", "hashCodeFloat", 1);
        assertEquals(1, relevantJumpInsns.size());
        foo();
    }

    @Test
    public void findAssignmentGuardForAliasedByteWithDefault() {
        analyseJumpInstructionsFor(AliasedByteWithDefault.class, "hash", "hashCodeByte", 1);
        assertEquals(1, relevantJumpInsns.size());
    }

    @Test
    public void findAssignmentGuardForJavaLangString() {
        initialiseAll(String.class, "hash", "hashCode");
        for (final ControlFlowBlock cfb : controlFlowBlocks.values()) {
            if (cfb.containsConditionCheck()) {
                collectRelevantJumpInstructions(getAllJumpInsructionsOfBlock(cfb), cfb);
            }
        }
        assertEquals(1, relevantJumpInsns.size());
    }

    @Test
    public void findAssignmentGuardForAliasedIntegerWithDefault() {
        analyseJumpInstructionsFor(AliasedIntegerWithDefault.class, "someNumber", "getSomeNumber", 1);
        analyseJumpInstructionsFor(AliasedIntegerWithDefault.class, "someNumber", "getSomeNumber", 4);
        assertEquals(2, relevantJumpInsns.size());
    }

    @Test
    public void findAssignmentGuardForAliasedIntegerWithSemantic() {
        analyseJumpInstructionsFor(AliasedIntegerWithSemantic.class, "cachedValue", "getMessageLength", 1);
        assertEquals(1, relevantJumpInsns.size());
        foo();
    }

    @Test
    public void findAssignmentGuardForIntegerWithSemantic() {
        analyseJumpInstructionsFor(IntegerWithSemantic.class, "hash", "hashCode", 0);
        assertEquals(1, relevantJumpInsns.size());
        foo();
    }

    @Test
    public void findAssignmentGuardForObjectWithDefault() {
        analyseJumpInstructionsFor(ObjectWithDefault.class, "hash", "hashCodeObject", 0);
        assertEquals(1, relevantJumpInsns.size());
        foo();
    }

    @Test
    public void findAssignmentGuardForAliasedIntegerWithDefaultDcli() {
        analyseJumpInstructionsFor(de.htwg_konstanz.jia.lazyinitialisation.doublecheck.AliasedIntegerWithDefault.class,
                "someNumber", "getSomeNumber", 1);
        analyseJumpInstructionsFor(de.htwg_konstanz.jia.lazyinitialisation.doublecheck.AliasedIntegerWithDefault.class,
                "someNumber", "getSomeNumber", 4);
        assertEquals(2, relevantJumpInsns.size());
        foo();
    }

    // Ueberpruefung der Sprunganweisung

    

    public void foo() {
        for (final Entry<Integer, Map<Integer, JumpInsnNode>> blocksWithRelevantJumpInsn : relevantJumpInsns.entrySet()) {
            final ControlFlowBlock blockWithRelevantJumpInsn = controlFlowBlocks.get(blocksWithRelevantJumpInsn.getKey());
            final Map<Integer, JumpInsnNode> relevantJumpInsns = blocksWithRelevantJumpInsn.getValue();
            for (final Entry<Integer, JumpInsnNode> relevantJumpInsnWithIndex : relevantJumpInsns.entrySet()) {
                final JumpInsnNode relevantJumpInsn = relevantJumpInsnWithIndex.getValue();
                final int indexOfPredecessorInstruction = relevantJumpInsnWithIndex.getKey() - 1;
                final List<AbstractInsnNode> blockInstructions = blockWithRelevantJumpInsn.getInstructions();
                final AbstractInsnNode predecessorInstruction = blockInstructions.get(indexOfPredecessorInstruction);
                if (isOneValueJumpInstruction(relevantJumpInsn)) {
                    if (checksAgainstZero(relevantJumpInsn)) {
                        if (isGetfieldForVariable(predecessorInstruction)) {
                            // passt
                            System.out.println("Passt.");
                        } else if (isLoadInstructionForAlias(blockWithRelevantJumpInsn, predecessorInstruction)) {
                                // TODO
                            
                        } else if (isComparisonInsn(predecessorInstruction)) {
                            final int indexOfPreComparisonInsn = indexOfPredecessorInstruction - 1;
                            final AbstractInsnNode predecessorOfComparisonInsn = blockInstructions.get(indexOfPreComparisonInsn);
                            if (isGetfieldForVariable(predecessorOfComparisonInsn)) {
                                final int indexOfGetfieldPredecessorInsn = indexOfPreComparisonInsn - 2;
                                final AbstractInsnNode predecessorOfGetfieldInsn = blockInstructions.get(indexOfGetfieldPredecessorInsn);
                                final UnknownTypeValue comparisonValue = getComparisonValue(predecessorOfGetfieldInsn);
                                if (null != comparisonValue) {
                                    if (possibleInitialValuesForVariable.contains(comparisonValue)) {
                                        // passt
                                        System.out.println("Passt.");
                                    } else {
                                        // nicht korrekt verzoegert initialisiert
                                        System.out.println("Nicht korrekt verzoegert initialisiert.");
                                    }
                                } else {
                                    // TODO Was passiert hier?
                                }
                            } else if (isLoadInstructionForAlias(blockWithRelevantJumpInsn, predecessorOfComparisonInsn)) {
                                final int indexOfLoadInsnPredecessor = indexOfPreComparisonInsn - 1;
                                final AbstractInsnNode predecessorOfLoadInsn = blockInstructions.get(indexOfLoadInsnPredecessor);
                                final UnknownTypeValue comparisonValue = getComparisonValue(predecessorOfLoadInsn);
                                if (null != comparisonValue) {
                                    if (possibleInitialValuesForVariable.contains(comparisonValue)) {
                                        // passt
                                        System.out.println("Passt.");
                                    } else {
                                        // nicht korrekt verzoegert initialisiert
                                        System.out.println("Nicht korrekt verzoegert initialisiert.");
                                    }
                                }
                            }
                        }
                    } else if (checksAgainstNull(relevantJumpInsn)) {
                        
                    } else if (checksAgainstNonNull(relevantJumpInsn)) {
                        if (isGetfieldForVariable(predecessorInstruction)) {
                            final UnknownTypeValue nullValue = UnknownTypeValueDefault.getInstanceForNull();
                            if (possibleInitialValuesForVariable.contains(nullValue)) {
                             // passt
                                System.out.println("Passt.");
                            } else {
                             // nicht korrekt verzoegert initialisiert
                                System.out.println("Nicht korrekt verzoegert initialisiert.");
                            }
                        }
                    }
                } else if (isTwoValuesJumpInstruction(relevantJumpInsn)) {
                    if (isGetfieldForVariable(predecessorInstruction)) {
                        final int indexOfStackPutInsn = indexOfPredecessorInstruction - 2;
                        final AbstractInsnNode stackPutInsn = blockInstructions.get(indexOfStackPutInsn);
                        final UnknownTypeValue comparisonValue = getComparisonValue(stackPutInsn);
                        if (possibleInitialValuesForVariable.contains(comparisonValue)) {
                            // passt
                            System.out.println("Passt.");
                        } else {
                            // nicht korrekt verzoegert initialisiert
                            System.out.println("Nicht korrekt verzoegert initialisiert.");
                        }
                    } else if (isLoadInstructionForAlias(blockWithRelevantJumpInsn, predecessorInstruction)) {
                        final int indexOfLoadInsnPredecessor = indexOfPredecessorInstruction - 1;
                        final AbstractInsnNode predecessorOfLoadInsn = blockInstructions.get(indexOfLoadInsnPredecessor);
                        final UnknownTypeValue comparisonValue = getComparisonValue(predecessorOfLoadInsn);
                        if (null != comparisonValue) {
                            if (possibleInitialValuesForVariable.contains(comparisonValue)) {
                                // passt
                                System.out.println("Passt.");
                            } else {
                                // nicht korrekt verzoegert initialisiert
                                System.out.println("Nicht korrekt verzoegert initialisiert.");
                            }
                        }
                    }
                }
            }
        }
    }

    private UnknownTypeValue getComparisonValue(final AbstractInsnNode insn) {
        UnknownTypeValue result = null;
        if (AbstractInsnNode.INSN == insn.getType()) {
            final Opcode opcode = Opcode.forInt(insn.getOpcode());
            result = opcode.stackValue();
        } else if (AbstractInsnNode.LDC_INSN == insn.getType()) {
            final LdcInsnNode ldcInsn = (LdcInsnNode) insn;
            result = UnknownTypeValueDefault.getInstance(ldcInsn.cst);
        } else if (AbstractInsnNode.INT_INSN == insn.getType()) {
            final IntInsnNode intInsnNode = (IntInsnNode) insn;
            result = UnknownTypeValueDefault.getInstance(intInsnNode.operand);
        }
        return result;
    }

    private boolean isOneValueJumpInstruction(final AbstractInsnNode insn) {
        final IntegerSetBuilder b = IntegerSetBuilder.getInstance();
        b.add(IFEQ).add(IFNE).add(IFLT).add(IFGE).add(IFGT).add(IFLE).add(IFNULL).add(IFNONNULL);
        final Set<Integer> oneValueJumpInstructions = b.build();
        return oneValueJumpInstructions.contains(insn.getOpcode());
    }

    private boolean checksAgainstZero(final JumpInsnNode jumpInsruction) {
        final IntegerSetBuilder b = IntegerSetBuilder.getInstance();
        b.add(IFEQ).add(IFNE).add(IFLT).add(IFGE).add(IFGT).add(IFLE);
        final Set<Integer> zeroChecks = b.build();
        return zeroChecks.contains(jumpInsruction.getOpcode());
    }

    private boolean checksAgainstNull(final JumpInsnNode jumpInstruction) {
        return IFNULL == jumpInstruction.getOpcode();
    }

    private boolean checksAgainstNonNull(final JumpInsnNode jumpInstruction) {
        return IFNONNULL == jumpInstruction.getOpcode();
    }

    private boolean isTwoValuesJumpInstruction(final AbstractInsnNode insn) {
        final IntegerSetBuilder b = IntegerSetBuilder.getInstance();
        b.add(IF_ICMPEQ).add(IF_ICMPNE).add(IF_ICMPLT).add(IF_ICMPGE).add(IF_ICMPGT).add(IF_ICMPLE);
        b.add(IF_ACMPEQ).add(IF_ACMPNE);
        final Set<Integer> twoValuesJumpInstructions = b.build();
        return twoValuesJumpInstructions.contains(insn.getOpcode());
    }

}
