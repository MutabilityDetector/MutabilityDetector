/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import static de.htwg_konstanz.jia.lazyinitialisation.AssignmentGuardFinder.CoversMatcher.covers;
import static org.apache.commons.lang3.Validate.notEmpty;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.objectweb.asm.Opcodes.*;

import java.util.*;
import java.util.Map.Entry;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import de.htwg_konstanz.jia.lazyinitialisation.ControlFlowBlock.ControlFlowBlockFactory;
import de.htwg_konstanz.jia.lazyinitialisation.VariableSetterCollection.Setters;
import de.htwg_konstanz.jia.lazyinitialisation.doublecheck.AliasedIntegerWithDefault;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.WithAlias;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.WithoutAlias;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 20.02.2013
 */
public final class AssignmentGuardFinder {

    private static final class Reason {
        
        private final ConvenienceClassNode ccn;
        private final List<ControlFlowBlock> cfbs;
        private final List<Integer> indicesOfRelevantJumpInstructions;
        private String variableName;
    
        public Reason(final Class<?> klasse) {
            ccn = createConvenienceClassNodeFor(klasse);
            cfbs = new ArrayList<ControlFlowBlock>();
            indicesOfRelevantJumpInstructions = new ArrayList<Integer>();
            variableName = "";
        }
    
        public Reason forMethod(final String methodName, final Type returnType,
                final Type... argumentTypes) {
            cfbs.addAll(ccn.getControlFlowBlocksForMethod("hashCodeFloat", Type.FLOAT_TYPE));
            return this;
        }
    
        public Reason andVariable(final String theVariableName) {
            variableName = theVariableName;
            return this;
        }
    
        public int numberOfRelevantJumpInstructions() {
            collectIndicesOfRelevantJumpInstructions();
            return indicesOfRelevantJumpInstructions.size();
        }
    
        private void collectIndicesOfRelevantJumpInstructions() {
            for (final ControlFlowBlock controlFlowBlock : cfbs) {
                final List<JumpInsn> conditionCheckInstructions = controlFlowBlock.getConditionCheckInstructions();
                for (final JumpInsn jumpInsn : conditionCheckInstructions) {
                    addIfRelevantJumpInstruction(controlFlowBlock, jumpInsn);
                }
            }
        }
    
        private void addIfRelevantJumpInstruction(final ControlFlowBlock controlFlowBlock, final JumpInsn jumpInsn) {
            final RelevantJumpInstructionFinder jif = RelevantJumpInstructionFinder.newInstance(variableName);
            if (jif.isRelevantJumpInstruction(jumpInsn.getIndexOfJumpInsn(), controlFlowBlock)) {
                indicesOfRelevantJumpInstructions.add(jumpInsn.getIndexOfJumpInsn());
            }
        }
    
        public ControlFlowBlock block(final int theBlockNumber) {
            return cfbs.get(theBlockNumber);
        }
    
        public int indexOfRelevantJumpInstruction() {
            return indicesOfRelevantJumpInstructions.get(0);
        }
    
    } // class Reason


    static final class CoversMatcher extends TypeSafeMatcher<ControlFlowBlock> {
    
        private final int indexOfRelevantJumpInstruction;
    
        private CoversMatcher(final int index) {
            indexOfRelevantJumpInstruction = index;
        }
    
        public static CoversMatcher covers(final int indexOfRelevantJumpInstruction) {
            return new CoversMatcher(indexOfRelevantJumpInstruction);
        }
    
        @Override
        public void describeTo(final Description description) {
            description.appendText("not");            
        }
    
        @Override
        protected boolean matchesSafely(final ControlFlowBlock controlFlowBlock) {
            return controlFlowBlock.covers(indexOfRelevantJumpInstruction);
        }
        
    } // class CoversMatcher


    private static final class RelevantJumpInstructionFinder {
        private final String variableName;
        
        private RelevantJumpInstructionFinder(final String theVariableName) {
            variableName = theVariableName;
        }
    
        public static RelevantJumpInstructionFinder newInstance(final String variableName) {
            return new RelevantJumpInstructionFinder(notEmpty(variableName));
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
            final List<AbstractInsnNode> instructions = blockWithJumpInsn.getBlockInstructions();
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
    
    } // class RelevantJumpInstructionFinder


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
    public void findAssignmentGuardForValidIntegerWithJvmInitial() {
        analyseJumpInstructionsFor(WithoutAlias.WithJvmInitialValue.IntegerValid.class, "hash", "hashCode", 0);
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
        final ConvenienceClassNode ccn = createConvenienceClassNodeFor(klasse);
        controlFlowBlocks = initialiseControlFlowBlocksFor(ccn, methodName);
        possibleInitialValuesForVariable = initialisePossibleInitialValuesFor(ccn, theVariableName);
    }

    private static ConvenienceClassNode createConvenienceClassNodeFor(final Class<?> klasse) {
        final ClassNodeFactory factory = ClassNodeFactory.getInstance();
        return factory.convenienceClassNodeFor(klasse);
    }

    private static Map<Integer, ControlFlowBlock> initialiseControlFlowBlocksFor(final ConvenienceClassNode ccn,
            final String methodName) {
        final List<MethodNode> methods = ccn.findMethodByName(methodName);
        final MethodNode method = methods.get(0);
        if (null != method) {
            final ControlFlowBlockFactory cfbFactory = ControlFlowBlockFactory.newInstance(ccn.name(), method);
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
        final List<AbstractInsnNode> instructions = blockWithJumpInsn.getBlockInstructions();
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
        final List<AbstractInsnNode> instructions = blockWithJumpInsn.getBlockInstructions();
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
//        final Set<Integer> compareInstructions = getComparisonInstructions();
//        final Integer opcode = Integer.valueOf(abstractInsnNode.getOpcode());
//        return compareInstructions.contains(opcode);
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

    private boolean isLoadInstructionForAlias(final AbstractInsnNode insn, final Alias alias) {
        boolean result = false;
        if (AbstractInsnNode.VAR_INSN == insn.getType()) {
            final VarInsnNode loadInstruction = (VarInsnNode) insn;
            result = loadInstruction.var == alias.localVariable;
        }
        return result;
    }

    @Test
    public void findAssignmentGuardForValidFloatWithJvmInitialValue() {
        final Class<?> klasse = WithoutAlias.WithJvmInitialValue.FloatValid.class;
        final Reason r = new Reason(klasse).forMethod("hashCodeFloat", Type.FLOAT_TYPE).andVariable("hash");
        assertThat(r.numberOfRelevantJumpInstructions(), is(1));
        assertThat(r.block(0), covers(r.indexOfRelevantJumpInstruction()));
        foo();
    }

    @Test
    public void findAssignmentGuardForAliasedValidFloatWithJvmInitialValue() {
//        analyseJumpInstructionsFor(WithAlias.WithJvmInitialValue.FloatValid.class, "hash", "hashCodeFloat", 1);
//        assertEquals(1, relevantJumpInsns.size());
        final Class<?> klasse = WithAlias.WithJvmInitialValue.FloatValid.class;
        final Reason r = new Reason(klasse).forMethod("hashCodeFloat", Type.FLOAT_TYPE).andVariable("hash");
        assertThat(r.numberOfRelevantJumpInstructions(), is(1));
        assertThat(r.block(0), covers(r.indexOfRelevantJumpInstruction()));
        foo();
    }

    @Test
    public void findAssignmentGuardForAliasedValidByteWithJvmInitialValue() {
        analyseJumpInstructionsFor(WithAlias.WithJvmInitialValue.ByteValid.class, "hash", "hashCodeByte", 1);
        assertEquals(1, relevantJumpInsns.size());
        foo();
    }

    @Test
    public void findAssignmentGuardForValidByteWithJvmInitial() {
        analyseJumpInstructionsFor(WithAlias.WithJvmInitialValue.ByteValid.class, "hash", "hashCodeByte", 1);
        assertEquals(1, relevantJumpInsns.size());
        foo();
    }

    @Test
    public void findAssignmentGuardForAliasedValidShortWithJvmInitialValue() {
        analyseJumpInstructionsFor(WithAlias.WithJvmInitialValue.ShortValid.class, "hash", "hashCodeShort", 1);
        assertEquals(1, relevantJumpInsns.size());
        foo();
    }

    @Test
    public void findAssignmentGuardForAliasedValidStringWithJvmInitialValue() {
        analyseJumpInstructionsFor(WithAlias.WithJvmInitialValue.StringValid.class, "hash", "hashCodeString", 1);
        assertEquals(1, relevantJumpInsns.size());
        foo();
    }

    @Test
    public void findAssignmentGuardForAliasedValidStringWithCustomInitialValue() {
        analyseJumpInstructionsFor(WithAlias.WithCustomInitialValue.StringValid.class, "hash", "hashCodeString", 1);
        assertEquals(1, relevantJumpInsns.size());
        foo();
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
    public void findAssignmentGuardForAliasedValidIntegerWithJvmInitialValue() {
        analyseJumpInstructionsFor(AliasedIntegerWithDefault.class, "someNumber", "getSomeNumber", 1);
        analyseJumpInstructionsFor(AliasedIntegerWithDefault.class, "someNumber", "getSomeNumber", 4);
        assertEquals(2, relevantJumpInsns.size());
    }

    @Test
    public void findAssignmentGuardForAliasedValidIntegerWithCustomInitialValue() {
        analyseJumpInstructionsFor(WithAlias.WithCustomInitialValue.IntegerValid.class, "cachedValue",
                "getMessageLength", 1);
        assertEquals(1, relevantJumpInsns.size());
        foo();
    }

    @Test
    public void findAssignmentGuardForValidIntegerWithCustomInitialValue() {
        analyseJumpInstructionsFor(WithoutAlias.WithCustomInitialValue.IntegerValid.class, "hash", "hashCode", 0);
        assertEquals(1, relevantJumpInsns.size());
        foo();
    }

    @Test
    public void findAssignmentGuardForValidCustomObjectWithJvmInitialValue() {
        analyseJumpInstructionsFor(WithoutAlias.WithJvmInitialValue.CustomObjectValid.class, "someObject",
                "hashCodeSomeObject", 0);
        assertEquals(1, relevantJumpInsns.size());
        foo();
    }

    @Test
    public void findAssignmentGuardForAliasedValidIntegerWithDefaultDcli() {
        analyseJumpInstructionsFor(de.htwg_konstanz.jia.lazyinitialisation.doublecheck.AliasedIntegerWithDefault.class,
                "someNumber", "getSomeNumber", 1);
        analyseJumpInstructionsFor(de.htwg_konstanz.jia.lazyinitialisation.doublecheck.AliasedIntegerWithDefault.class,
                "someNumber", "getSomeNumber", 4);
        assertEquals(2, relevantJumpInsns.size());
        foo();
    }

    @Test
    public void findAssignmentGuardForInvalidFloatObjectWithMultipleCustomInitialValues() {
        final Class<?> klasse = WithoutAlias.WithCustomInitialValue.FloatInvalidWithMultipleInitialValues.class;
        final Reason r = new Reason(klasse).forMethod("hashCodeFloat", Type.FLOAT_TYPE).andVariable("hash");
        assertThat(r.numberOfRelevantJumpInstructions(), is(1));
        assertThat(r.block(0), covers(r.indexOfRelevantJumpInstruction()));
        foo();
    }

    @Test
    public void findIndexOfRelevantJumpInstructionForLazyMethodOfAliasedValidFloatWithJvmInitialValue() {
        final Class<?> klasse = WithoutAlias.WithJvmInitialValue.FloatValid.class;
        final Reason r = new Reason(klasse).forMethod("hashCodeFloat", Type.FLOAT_TYPE).andVariable("hash");
        assertThat(r.numberOfRelevantJumpInstructions(), is(1));
        assertThat(r.block(0), covers(r.indexOfRelevantJumpInstruction()));
    }

    // Ueberpruefung der Sprunganweisung

    

    public void foo() {
        for (final Entry<Integer, Map<Integer, JumpInsnNode>> blocksWithRelevantJumpInsn : relevantJumpInsns.entrySet()) {
            final ControlFlowBlock blockWithRelevantJumpInsn = controlFlowBlocks.get(blocksWithRelevantJumpInsn.getKey());
            final Map<Integer, JumpInsnNode> relevantJumpInsns = blocksWithRelevantJumpInsn.getValue();
            for (final Entry<Integer, JumpInsnNode> relevantJumpInsnWithIndex : relevantJumpInsns.entrySet()) {
                final JumpInsnNode relevantJumpInsn = relevantJumpInsnWithIndex.getValue();
                final int indexOfPredecessorInstruction = relevantJumpInsnWithIndex.getKey() - 1;
                final List<AbstractInsnNode> blockInstructions = blockWithRelevantJumpInsn.getBlockInstructions();
                final AbstractInsnNode predecessorInstruction = blockInstructions.get(indexOfPredecessorInstruction);
                if (isOneValueJumpInstruction(relevantJumpInsn)) {
                    if (checksAgainstZero(relevantJumpInsn)) {
                        if (isGetfieldForVariable(predecessorInstruction)) {
                            // passt
                            System.out.println("Passt.");
                        } else if (isLoadInstructionForAlias(blockWithRelevantJumpInsn, predecessorInstruction)) {
                            // passt
                            System.out.println("Passt.");
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
                        // TODO Was passiert hier?
                    } else if (checksAgainstNonNull(relevantJumpInsn)) {
                        if (!possibleInitialValuesForVariable.contains(UnknownTypeValueDefault.getInstanceForNull())) {
                            // nicht korrekt verzoegert initialisiert
                            System.out.println("Nicht korrekt verzoegert initialisiert.");
                        } else if (isGetfieldForVariable(predecessorInstruction)) {
                            final UnknownTypeValue nullValue = UnknownTypeValueDefault.getInstanceForNull();
                            if (possibleInitialValuesForVariable.contains(nullValue)) {
                                // passt
                                System.out.println("Passt.");
                            } else {
                                // nicht korrekt verzoegert initialisiert
                                System.out.println("Nicht korrekt verzoegert initialisiert.");
                            }
                        } else if (isLoadInstructionForAlias(blockWithRelevantJumpInsn, predecessorInstruction)) {
                            // passt
                            System.out.println("Passt.");
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
