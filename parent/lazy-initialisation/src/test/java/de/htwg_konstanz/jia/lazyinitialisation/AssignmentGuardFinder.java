/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import static de.htwg_konstanz.jia.lazyinitialisation.AssignmentGuardFinder.CoversMatcher.covers;
import static org.apache.commons.lang3.Validate.notEmpty;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.objectweb.asm.Opcodes.*;

import java.util.*;

import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import de.htwg_konstanz.jia.lazyinitialisation.VariableSetterCollection.Setters;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.WithAlias;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.WithoutAlias;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.WithoutAlias.WithJvmInitialValue.CustomObjectValid.SomeObject;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 20.02.2013
 */
public final class AssignmentGuardFinder {

    @NotThreadSafe
    private static final class Reason {

        private final ConvenienceClassNode ccn;
        private final List<ControlFlowBlock> cfbs;
        private final List<Integer> indicesOfRelevantJumpInstructions;
        private String variableName;
        private final Set<UnknownTypeValue> possibleInitialValuesForVariable;

        public Reason(final Class<?> klasse) {
            ccn = createConvenienceClassNodeFor(klasse);
            cfbs = new ArrayList<ControlFlowBlock>();
            indicesOfRelevantJumpInstructions = new ArrayList<Integer>();
            variableName = "";
            possibleInitialValuesForVariable = new HashSet<UnknownTypeValue>();
        }
    
        public Reason forMethod(final String methodName, final Type returnType,
                final Type... argumentTypes) {
            cfbs.addAll(ccn.getControlFlowBlocksForMethod(methodName, returnType, argumentTypes));
            return this;
        }
    
        public Reason andVariable(final String theVariableName) {
            variableName = notEmpty(theVariableName);
            findPossibleInitialValuesForVariable();
            return this;
        }

        private void findPossibleInitialValuesForVariable() {
            final VariableSetterCollection variableSetterCollection = ccn.getVariableSetterCollection();
            final FieldNode variable = ccn.findVariableWithName(variableName);
            final Setters setters = variableSetterCollection.getSettersFor(variable);
            final InitialValueFinder initialValueFinder = InitialValueFinder.newInstance(variable, setters);
            initialValueFinder.run();
            possibleInitialValuesForVariable.addAll(initialValueFinder.getPossibleInitialValues());
        }

        public String variableName() {
            return variableName;
        }

        public Set<UnknownTypeValue> initialValues() {
            return possibleInitialValuesForVariable;
        }

        public int numberOfRelevantJumpInstructions() {
            collectIndicesOfRelevantJumpInstructions();
            return indicesOfRelevantJumpInstructions.size();
        }

        private void collectIndicesOfRelevantJumpInstructions() {
            for (final ControlFlowBlock cfb : cfbs) {
                for (final JumpInsn jumpInsn : cfb.getEffectiveJumpInstructionsForVariable(variableName)) {
                    indicesOfRelevantJumpInstructions.add(jumpInsn.getIndexWithinMethod());
                }
            }
        }

        public ControlFlowBlock block(final int theBlockNumber) {
            return cfbs.get(theBlockNumber);
        }

        public List<ControlFlowBlock> blocks() {
            return cfbs;
        }

        public Collection<Integer> relevantJumpInstructions() {
            return indicesOfRelevantJumpInstructions;
        }

    } // class Reason


    @ThreadSafe
    static final class CoversMatcher extends TypeSafeMatcher<ControlFlowBlock> {

        private final Collection<Integer> indicesOfRelevantJumpInstructions;

        private CoversMatcher(final Collection<Integer> theIndicesOfRelevantJumpInstructions) {
            indicesOfRelevantJumpInstructions = Collections.unmodifiableCollection(theIndicesOfRelevantJumpInstructions);
        }

        public static CoversMatcher covers(final int indexOfRelevantJumpInstruction) {
            final Set<Integer> integerSet = new HashSet<Integer>(1);
            integerSet.add(Integer.valueOf(indexOfRelevantJumpInstruction));
            return new CoversMatcher(integerSet);
        }

        public static CoversMatcher covers(final Collection<Integer> indicesOfRelevantJumpInstructions) {
            return new CoversMatcher(indicesOfRelevantJumpInstructions);
        }

        @Override
        public void describeTo(final Description description) {
            description.appendText("block covers all instructions with block indices ");
            description.appendValue(indicesOfRelevantJumpInstructions);            
        }

        @Override
        protected boolean matchesSafely(final ControlFlowBlock controlFlowBlock) {
            return controlFlowBlock.coversOneOf(indicesOfRelevantJumpInstructions);
        }

    } // class CoversMatcher


    @Test
    public void findAssignmentGuardForValidIntegerWithJvmInitial() {
        final Class<?> klasse = WithoutAlias.WithJvmInitialValue.IntegerValid.class;
        final Reason r = new Reason(klasse).forMethod("hashCode", Type.INT_TYPE).andVariable("hash");
        assertThat(r.numberOfRelevantJumpInstructions(), is(1));
        assertThat(r.block(0), covers(r.relevantJumpInstructions()));
        verifyComparativeValueOfRelevantConditionCheck(r);
    }

    @Test
    public void findAssignmentGuardForValidFloatWithJvmInitialValue() {
        final Class<?> klasse = WithoutAlias.WithJvmInitialValue.FloatValid.class;
        final Reason r = new Reason(klasse).forMethod("hashCodeFloat", Type.FLOAT_TYPE).andVariable("hash");
        assertThat(r.numberOfRelevantJumpInstructions(), is(1));
        assertThat(r.block(0), covers(r.relevantJumpInstructions()));
        verifyComparativeValueOfRelevantConditionCheck(r);
    }

    @Test
    public void findAssignmentGuardForAliasedValidFloatWithJvmInitialValue() {
        final Class<?> klasse = WithAlias.WithJvmInitialValue.FloatValid.class;
        final Reason r = new Reason(klasse).forMethod("hashCodeFloat", Type.FLOAT_TYPE).andVariable("hash");
        assertThat(r.numberOfRelevantJumpInstructions(), is(1));
        assertThat(r.block(1), covers(r.relevantJumpInstructions()));
        verifyComparativeValueOfRelevantConditionCheck(r);
    }

    @Test
    public void findAssignmentGuardForAliasedValidByteWithJvmInitialValue() {
        final Class<?> klasse = WithAlias.WithJvmInitialValue.ByteValid.class;
        final Reason r = new Reason(klasse).forMethod("hashCodeByte", Type.BYTE_TYPE).andVariable("hash");
        assertThat(r.numberOfRelevantJumpInstructions(), is(1));
        assertThat(r.block(1), covers(r.relevantJumpInstructions()));
        verifyComparativeValueOfRelevantConditionCheck(r);
    }

    @Test
    public void findAssignmentGuardForValidCharWithJvmInitial() {
        final Class<?> klasse = WithoutAlias.WithJvmInitialValue.CharValid.class;
        final Reason r = new Reason(klasse).forMethod("hashCodeChar", Type.CHAR_TYPE).andVariable("hash");
        assertThat(r.numberOfRelevantJumpInstructions(), is(1));
        assertThat(r.block(0), covers(r.relevantJumpInstructions()));
        verifyComparativeValueOfRelevantConditionCheck(r);
    }

    @Test
    public void findAssignmentGuardForAliasedValidShortWithJvmInitialValue() {
        final Class<?> klasse = WithAlias.WithJvmInitialValue.ShortValid.class;
        final Reason r = new Reason(klasse).forMethod("hashCodeShort", Type.SHORT_TYPE).andVariable("hash");
        assertThat(r.numberOfRelevantJumpInstructions(), is(1));
        assertThat(r.block(1), covers(r.relevantJumpInstructions()));
        verifyComparativeValueOfRelevantConditionCheck(r);
    }

    @Test
    public void findAssignmentGuardForAliasedValidStringWithJvmInitialValue() {
        final Class<?> klasse = WithAlias.WithJvmInitialValue.StringValid.class;
        final Reason r = new Reason(klasse).forMethod("hashCodeString", Type.getType(String.class)).andVariable("hash");
        assertThat(r.numberOfRelevantJumpInstructions(), is(1));
        assertThat(r.block(1), covers(r.relevantJumpInstructions()));
        verifyComparativeValueOfRelevantConditionCheck(r);
    }

    @Test
    public void findAssignmentGuardForAliasedValidStringWithCustomInitialValue() {
        final Class<?> klasse = WithAlias.WithCustomInitialValue.StringValid.class;
        final Reason r = new Reason(klasse).forMethod("hashCodeString", Type.getType(String.class)).andVariable("hash");
        assertThat(r.numberOfRelevantJumpInstructions(), is(1));
        assertThat(r.block(1), covers(r.relevantJumpInstructions()));
        verifyComparativeValueOfRelevantConditionCheck(r);
    }

    @Test
    public void findAssignmentGuardForJavaLangString() {
        final Class<?> klasse = String.class;
        final Reason r = new Reason(klasse).forMethod("hashCode", Type.INT_TYPE).andVariable("hash");
        assertThat(r.numberOfRelevantJumpInstructions(), is(1));
    }

// TODO Löschen, da Double-Check-Idiom
//
//    @Test
//    public void findAssignmentGuardForAliasedValidIntegerWithJvmInitialValue() {
//        analyseJumpInstructionsFor(AliasedIntegerWithDefault.class, "someNumber", "getSomeNumber", 1);
//        analyseJumpInstructionsFor(AliasedIntegerWithDefault.class, "someNumber", "getSomeNumber", 4);
//        assertEquals(2, relevantJumpInsns.size());
//    }

    @Test
    public void findAssignmentGuardForAliasedValidIntegerWithCustomInitialValue() {
        final Class<?> klasse = WithAlias.WithCustomInitialValue.IntegerValid.class;
        final Reason r = new Reason(klasse).forMethod("getMessageLength", Type.INT_TYPE).andVariable("cachedValue");
        assertThat(r.numberOfRelevantJumpInstructions(), is(1));
        assertThat(r.block(1), covers(r.relevantJumpInstructions()));
        verifyComparativeValueOfRelevantConditionCheck(r);
    }

    @Test
    public void findAssignmentGuardForValidIntegerWithCustomInitialValue() {
        final Class<?> klasse = WithoutAlias.WithCustomInitialValue.IntegerValid.class;
        final Reason r = new Reason(klasse).forMethod("hashCode", Type.INT_TYPE).andVariable("hash");
        assertThat(r.numberOfRelevantJumpInstructions(), is(1));
        assertThat(r.block(0), covers(r.relevantJumpInstructions()));
        verifyComparativeValueOfRelevantConditionCheck(r);
    }

    @Test
    public void findAssignmentGuardForValidCustomObjectWithJvmInitialValue() {
        final Reason r = new Reason(WithoutAlias.WithJvmInitialValue.CustomObjectValid.class).forMethod(
                "hashCodeSomeObject", Type.getType(SomeObject.class)).andVariable("someObject");
        assertThat(r.numberOfRelevantJumpInstructions(), is(1));
        assertThat(r.block(0), covers(r.relevantJumpInstructions()));
        verifyComparativeValueOfRelevantConditionCheck(r);
    }

    @Test
    public void findAssignmentGuardForAliasedValidIntegerWithDefaultDcli() {
        final Reason r = new Reason(de.htwg_konstanz.jia.lazyinitialisation.doublecheck.AliasedIntegerWithDefault.class)
                .forMethod("getSomeNumber", Type.INT_TYPE).andVariable("someNumber");
        assertThat(r.numberOfRelevantJumpInstructions(), is(2));
        assertThat(r.block(1), covers(r.relevantJumpInstructions()));
        verifyComparativeValueOfRelevantConditionCheck(r);
    }

    @Test
    public void findAssignmentGuardForInvalidFloatObjectWithMultipleCustomInitialValues() {
        final Class<?> klasse = WithoutAlias.WithCustomInitialValue.FloatInvalidWithMultipleInitialValues.class;
        final Reason r = new Reason(klasse).forMethod("hashCodeFloat", Type.FLOAT_TYPE).andVariable("hash");
        assertThat(r.numberOfRelevantJumpInstructions(), is(1));
        assertThat(r.block(0), covers(r.relevantJumpInstructions()));
        verifyComparativeValueOfRelevantConditionCheck(r);
    }

    @Test
    public void findIndexOfRelevantJumpInstructionForLazyMethodOfAliasedValidFloatWithJvmInitialValue() {
        final Class<?> klasse = WithoutAlias.WithJvmInitialValue.FloatValid.class;
        final Reason r = new Reason(klasse).forMethod("hashCodeFloat", Type.FLOAT_TYPE).andVariable("hash");
        assertThat(r.numberOfRelevantJumpInstructions(), is(1));
        assertThat(r.block(0), covers(r.relevantJumpInstructions()));
    }

    // Ueberpruefung der Sprunganweisung

    
    public void verifyComparativeValueOfRelevantConditionCheck(final Reason r) {
        final Set<UnknownTypeValue> possibleInitialValuesForVariable = r.initialValues();
        for (final ControlFlowBlock cfb : r.blocks()) {
            final List<AbstractInsnNode> blockInstructions = cfb.getBlockInstructions();
            for (final JumpInsn relevantJumpInsn : cfb.getEffectiveJumpInstructionsForVariable(r.variableName())) {
                final int indexOfPredecessorInstruction = relevantJumpInsn.getIndexWithinBlock() - 1;
                final AbstractInsnNode predecessorInstruction = cfb.getBlockInstructionForIndex(indexOfPredecessorInstruction);

                if (isOneValueJumpInstruction(relevantJumpInsn)) {
                    if (checksAgainstZero(relevantJumpInsn)) {
                        if (isGetfieldForVariable(predecessorInstruction, r.variableName())) {
                            // passt
                            System.out.println("Passt.");
                        } else if (isLoadInstructionForAlias(r.variableName(), cfb, predecessorInstruction)) {
                            // passt
                            System.out.println("Passt.");
                        } else if (isComparisonInsn(predecessorInstruction)) {
                            final int indexOfPreComparisonInsn = indexOfPredecessorInstruction - 1;
                            final AbstractInsnNode predecessorOfComparisonInsn = blockInstructions.get(indexOfPreComparisonInsn);
                            if (isGetfieldForVariable(predecessorOfComparisonInsn, r.variableName())) {
                                foo(indexOfPreComparisonInsn, blockInstructions, possibleInitialValuesForVariable);
                            } else if (isLoadInstructionForAlias(r.variableName(), cfb, predecessorOfComparisonInsn)) {
                                bar(indexOfPreComparisonInsn, blockInstructions, possibleInitialValuesForVariable);
                            }
                        }
                    } else if (checksAgainstNull(relevantJumpInsn)) {
                        // TODO Was passiert hier?
                    } else if (checksAgainstNonNull(relevantJumpInsn)) {
                        if (!possibleInitialValuesForVariable.contains(UnknownTypeValueDefault.getInstanceForNull())) {
                            // nicht korrekt verzoegert initialisiert
                            System.out.println("Nicht korrekt verzoegert initialisiert.");
                        } else if (isGetfieldForVariable(predecessorInstruction, r.variableName())) {
                            final UnknownTypeValue nullValue = UnknownTypeValueDefault.getInstanceForNull();
                            if (possibleInitialValuesForVariable.contains(nullValue)) {
                                // passt
                                System.out.println("Passt.");
                            } else {
                                // nicht korrekt verzoegert initialisiert
                                System.out.println("Nicht korrekt verzoegert initialisiert.");
                            }
                        } else if (isLoadInstructionForAlias(r.variableName(), cfb, predecessorInstruction)) {
                            // passt
                            System.out.println("Passt.");
                        }
                    }
                } else if (isTwoValuesJumpInstruction(relevantJumpInsn)) {
                    if (isGetfieldForVariable(predecessorInstruction, r.variableName())) {
                        foo(indexOfPredecessorInstruction, blockInstructions, possibleInitialValuesForVariable);
                    } else if (isLoadInstructionForAlias(r.variableName(), cfb, predecessorInstruction)) {
                        bar(indexOfPredecessorInstruction, blockInstructions, possibleInitialValuesForVariable);
                    }
                }
            }
        }
    }

    private void bar(final int indexOfPreComparisonInsn,
            final List<AbstractInsnNode> blockInstructions,
            final Set<UnknownTypeValue> possibleInitialValuesForVariable) {
        final int indexOfLoadInsnPredecessor = indexOfPreComparisonInsn - 1;
        final AbstractInsnNode predecessorOfLoadInsn = blockInstructions.get(indexOfLoadInsnPredecessor);
        final UnknownTypeValue comparativeValue = getComparativeValue(predecessorOfLoadInsn);
        if (possibleInitialValuesForVariable.contains(comparativeValue)) {
            // passt
            System.out.println("Passt.");
        } else {
            // nicht korrekt verzoegert initialisiert
            System.out.println("Nicht korrekt verzoegert initialisiert.");
        }
    }

    private void foo(final int indexOfPreComparisonInsn,
            final List<AbstractInsnNode> blockInstructions,
            final Set<UnknownTypeValue> possibleInitialValuesForVariable) {
        final int indexOfGetfieldPredecessorInsn = indexOfPreComparisonInsn - 2;
        final AbstractInsnNode predecessorOfGetfieldInsn = blockInstructions.get(indexOfGetfieldPredecessorInsn);
        final UnknownTypeValue comparativeValue = getComparativeValue(predecessorOfGetfieldInsn);
        if (possibleInitialValuesForVariable.contains(comparativeValue)) {
            // passt
            System.out.println("Passt.");
        } else {
            // nicht korrekt verzoegert initialisiert
            System.out.println("Nicht korrekt verzoegert initialisiert.");
        }
    }

    private static ConvenienceClassNode createConvenienceClassNodeFor(final Class<?> klasse) {
        final ClassNodeFactory factory = ClassNodeFactory.getInstance();
        return factory.convenienceClassNodeFor(klasse);
    }

    private static boolean isGetfieldForVariable(final AbstractInsnNode insn, final String variableName) {
        boolean result = false;
        if (Opcodes.GETFIELD == insn.getOpcode()) {
            final FieldInsnNode getfield = (FieldInsnNode) insn;
            result = variableName.equals(getfield.name);
        }
        return result;
    }

    private boolean isComparisonInsn(final AbstractInsnNode abstractInsnNode) {
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

    private boolean isLoadInstructionForAlias(final String variableName,
            final ControlFlowBlock blockWithJumpInsn,
            final AbstractInsnNode insn) {
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

    private UnknownTypeValue getComparativeValue(final AbstractInsnNode insn) {
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

    private boolean isOneValueJumpInstruction(final JumpInsn jumpInstruction) {
        switch (getOpcode(jumpInstruction)) {
        case IFEQ:
        case IFNE:
        case IFLT:
        case IFGE:
        case IFGT:
        case IFLE:
        case IFNULL:
        case IFNONNULL:
            return true;

        default:
            return false;
        }
    }

    private static int getOpcode(final JumpInsn jumpInstruction) {
        final JumpInsnNode jumpInsnNode = jumpInstruction.getJumpInsnNode();
        return jumpInsnNode.getOpcode();
    }

    private boolean checksAgainstZero(final JumpInsn jumpInstruction) {
        switch (getOpcode(jumpInstruction)) {
        case IFEQ:
        case IFNE:
        case IFLT:
        case IFGE:
        case IFGT:
        case IFLE:
            return true;
        default:
            return false;
        }
    }

    private boolean checksAgainstNull(final JumpInsn jumpInstruction) {
        return IFNULL == getOpcode(jumpInstruction);
    }

    private boolean checksAgainstNonNull(final JumpInsn jumpInstruction) {
        return IFNONNULL == getOpcode(jumpInstruction);
    }

    private boolean isTwoValuesJumpInstruction(final JumpInsn jumpInstruction) {
        switch (getOpcode(jumpInstruction)) {
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

}
