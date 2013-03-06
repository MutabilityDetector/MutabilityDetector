/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import static de.htwg_konstanz.jia.lazyinitialisation.AssignmentGuardFinder.CoversMatcher.covers;
import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.objectweb.asm.Opcodes.*;

import java.util.*;

import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
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
@RunWith(Enclosed.class)
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

        private static ConvenienceClassNode createConvenienceClassNodeFor(final Class<?> klasse) {
            final ClassNodeFactory factory = ClassNodeFactory.getInstance();
            return factory.convenienceClassNodeFor(klasse);
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


    public static final class ValidSingleCheckLazyInitialisationWithoutAlias {

        @Test
        public void integerWithJvmInitial() {
            final Class<?> klasse = WithoutAlias.WithJvmInitialValue.IntegerValid.class;
            final Reason r = new Reason(klasse).forMethod("hashCode", Type.INT_TYPE).andVariable("hash");
            assertThat(r.numberOfRelevantJumpInstructions(), is(1));
            assertThat(r.block(0), covers(r.relevantJumpInstructions()));
            assertTrue(checksAgainstAppropriateComparativeValue(r));
        }

        @Test
        public void floatWithJvmInitialValue() {
            final Class<?> klasse = WithoutAlias.WithJvmInitialValue.FloatValid.class;
            final Reason r = new Reason(klasse).forMethod("hashCodeFloat", Type.FLOAT_TYPE).andVariable("hash");
            assertThat(r.numberOfRelevantJumpInstructions(), is(1));
            assertThat(r.block(0), covers(r.relevantJumpInstructions()));
            assertTrue(checksAgainstAppropriateComparativeValue(r));
        }

        @Test
        public void charWithJvmInitial() {
            final Class<?> klasse = WithoutAlias.WithJvmInitialValue.CharValid.class;
            final Reason r = new Reason(klasse).forMethod("hashCodeChar", Type.CHAR_TYPE).andVariable("hash");
            assertThat(r.numberOfRelevantJumpInstructions(), is(1));
            assertThat(r.block(0), covers(r.relevantJumpInstructions()));
            assertTrue(checksAgainstAppropriateComparativeValue(r));
        }

        @Test
        public void objectWithJvmInitialValue() {
            final Reason r = new Reason(WithoutAlias.WithJvmInitialValue.ObjectValid.class).forMethod("hashCodeObject",
                    Type.getType(Object.class)).andVariable("hash");
            assertThat(r.numberOfRelevantJumpInstructions(), is(1));
            assertThat(r.block(0), covers(r.relevantJumpInstructions()));
            assertTrue(checksAgainstAppropriateComparativeValue(r));
        }

        @Test
        public void integerWithCustomInitialValue() {
            final Class<?> klasse = WithoutAlias.WithCustomInitialValue.IntegerValid.class;
            final Reason r = new Reason(klasse).forMethod("hashCode", Type.INT_TYPE).andVariable("hash");
            assertThat(r.numberOfRelevantJumpInstructions(), is(1));
            assertThat(r.block(0), covers(r.relevantJumpInstructions()));
            assertTrue(checksAgainstAppropriateComparativeValue(r));
        }

        @Test
        public void floatWithCustomInitialValue() {
            final Class<?> klasse = WithoutAlias.WithCustomInitialValue.FloatValid.class;
            final Reason r = new Reason(klasse).forMethod("hashCodeFloat", Type.FLOAT_TYPE).andVariable("hash");
            assertThat(r.numberOfRelevantJumpInstructions(), is(1));
            assertThat(r.block(0), covers(r.relevantJumpInstructions()));
            assertTrue(checksAgainstAppropriateComparativeValue(r));
        }

        @Test
        public void customObjectWithJvmInitialValue() {
            final Reason r = new Reason(WithoutAlias.WithJvmInitialValue.CustomObjectValid.class).forMethod(
                    "hashCodeSomeObject", Type.getType(SomeObject.class)).andVariable("someObject");
            assertThat(r.numberOfRelevantJumpInstructions(), is(1));
            assertThat(r.block(0), covers(r.relevantJumpInstructions()));
            assertTrue(checksAgainstAppropriateComparativeValue(r));
        }

    } // class TestsForValidSingleCheckLazyInitialisationWithoutAlias


    public static final class InvalidSingleCheckLazyInitialisationWithoutAlias {

        @Test
        public void charWithJvmInitialValue() {
            final Class<?> klasse = WithoutAlias.WithJvmInitialValue.CharInvalid.class;
            final Reason r = new Reason(klasse).forMethod("hashCodeChar", Type.CHAR_TYPE).andVariable("hash");
            assertThat(r.numberOfRelevantJumpInstructions(), is(0));
            assertFalse(checksAgainstAppropriateComparativeValue(r));
        }

        @Test
        public void integerWithJvmInitialValue() {
            final Class<?> klasse = WithoutAlias.WithJvmInitialValue.IntegerInvalid.class;
            final Reason r = new Reason(klasse).forMethod("hashCode", Type.INT_TYPE).andVariable("hash");
            assertThat(r.numberOfRelevantJumpInstructions(), is(1));
            assertThat(r.block(0), covers(r.relevantJumpInstructions()));
            assertFalse(checksAgainstAppropriateComparativeValue(r));
        }

        @Test
        public void floatWithJvmInitialValue() {
            final Class<?> klasse = WithoutAlias.WithJvmInitialValue.FloatInvalid.class;
            final Reason r = new Reason(klasse).forMethod("hashCodeFloat", Type.FLOAT_TYPE).andVariable("hash");
            assertThat(r.numberOfRelevantJumpInstructions(), is(1));
            assertThat(r.block(0), covers(r.relevantJumpInstructions()));
            assertFalse(checksAgainstAppropriateComparativeValue(r));
        }

        @Test
        public void objectWithJvmInitialValue() {
            final Reason r = new Reason(WithoutAlias.WithJvmInitialValue.ObjectInvalid.class).forMethod(
                    "hashCodeObject", Type.getType(Object.class)).andVariable("hash");
            // FIXME
            assertThat(r.numberOfRelevantJumpInstructions(), is(0));
            assertFalse(checksAgainstAppropriateComparativeValue(r));
        }

        @Test
        public void stringWithJvmInitialValue() {
            final Reason r = new Reason(WithoutAlias.WithJvmInitialValue.StringInvalid.class).forMethod(
                    "hashCodeString", Type.getType(String.class)).andVariable("hash");
            assertThat(r.numberOfRelevantJumpInstructions(), is(0));
            assertFalse(checksAgainstAppropriateComparativeValue(r));
        }

        @Test
        public void floatWithMultipleCustomInitialValues() {
            final Class<?> klasse = WithoutAlias.WithCustomInitialValue.FloatInvalidWithMultipleInitialValues.class;
            final Reason r = new Reason(klasse).forMethod("hashCodeFloat", Type.FLOAT_TYPE).andVariable("hash");
            assertThat(r.numberOfRelevantJumpInstructions(), is(1));
            assertThat(r.block(0), covers(r.relevantJumpInstructions()));
            assertFalse(checksAgainstAppropriateComparativeValue(r));
        }

        @Test
        public void integerWithCustomInitialValue() {
            final Class<?> klasse = WithoutAlias.WithCustomInitialValue.IntegerInvalid.class;
            final Reason r = new Reason(klasse).forMethod("hashCode", Type.INT_TYPE).andVariable("hash");
            assertThat(r.numberOfRelevantJumpInstructions(), is(1));
            assertThat(r.block(0), covers(r.relevantJumpInstructions()));
            assertFalse(checksAgainstAppropriateComparativeValue(r));
        }

        @Test
        public void stringWithCustomInitialValue() {
            final Reason r = new Reason(WithoutAlias.WithCustomInitialValue.StringInvalid.class).forMethod(
                    "hashCodeString", Type.getType(String.class)).andVariable("hash");
            assertThat(r.numberOfRelevantJumpInstructions(), is(0));
//            assertThat(r.block(0), covers(r.relevantJumpInstructions()));
            assertFalse(checksAgainstAppropriateComparativeValue(r));
        }

    } // class InvalidSingleCheckLazyInitialisationWithoutAlias


    public static final class ValidSingleCheckLazyInitialisationWithAlias {

        @Test
        public void byteWithJvmInitialValue() {
            final Class<?> klasse = WithAlias.WithJvmInitialValue.ByteValid.class;
            final Reason r = new Reason(klasse).forMethod("hashCodeByte", Type.BYTE_TYPE).andVariable("hash");
            assertThat(r.numberOfRelevantJumpInstructions(), is(1));
            assertThat(r.block(1), covers(r.relevantJumpInstructions()));
            assertTrue(checksAgainstAppropriateComparativeValue(r));
        }

        @Test
        public void shortWithJvmInitialValue() {
            final Class<?> klasse = WithAlias.WithJvmInitialValue.ShortValid.class;
            final Reason r = new Reason(klasse).forMethod("hashCodeShort", Type.SHORT_TYPE).andVariable("hash");
            assertThat(r.numberOfRelevantJumpInstructions(), is(1));
            assertThat(r.block(1), covers(r.relevantJumpInstructions()));
            assertTrue(checksAgainstAppropriateComparativeValue(r));
        }

        @Test
        public void floatWithJvmInitialValue() {
            final Class<?> klasse = WithAlias.WithJvmInitialValue.FloatValid.class;
            final Reason r = new Reason(klasse).forMethod("hashCodeFloat", Type.FLOAT_TYPE).andVariable("hash");
            assertThat(r.numberOfRelevantJumpInstructions(), is(1));
            assertThat(r.block(1), covers(r.relevantJumpInstructions()));
            assertTrue(checksAgainstAppropriateComparativeValue(r));
        }

        @Test
        public void javaLangString() {
            final Class<?> klasse = String.class;
            final Reason r = new Reason(klasse).forMethod("hashCode", Type.INT_TYPE).andVariable("hash");
            assertThat(r.numberOfRelevantJumpInstructions(), is(1));
        }

        @Test
        public void stringWithJvmInitialValue() {
            final Class<?> klasse = WithAlias.WithJvmInitialValue.StringValid.class;
            final Reason r = new Reason(klasse).forMethod("hashCodeString", Type.getType(String.class)).andVariable("hash");
            assertThat(r.numberOfRelevantJumpInstructions(), is(1));
            assertThat(r.block(1), covers(r.relevantJumpInstructions()));
            assertTrue(checksAgainstAppropriateComparativeValue(r));
        }

        @Test
        public void stringWithCustomInitialValue() {
            final Class<?> klasse = WithAlias.WithCustomInitialValue.StringValid.class;
            final Reason r = new Reason(klasse).forMethod("hashCodeString", Type.getType(String.class)).andVariable("hash");
            assertThat(r.numberOfRelevantJumpInstructions(), is(1));
            assertThat(r.block(1), covers(r.relevantJumpInstructions()));
            assertTrue(checksAgainstAppropriateComparativeValue(r));
        }

        @Test
        public void integerWithCustomInitialValue() {
            final Class<?> klasse = WithAlias.WithCustomInitialValue.IntegerValid.class;
            final Reason r = new Reason(klasse).forMethod("getMessageLength", Type.INT_TYPE).andVariable("cachedValue");
            assertThat(r.numberOfRelevantJumpInstructions(), is(1));
            assertThat(r.block(1), covers(r.relevantJumpInstructions()));
            assertTrue(checksAgainstAppropriateComparativeValue(r));
        }

    } // TestsForValidSingleCheckLazyInitialisationWithAlias


    public static final class ValidDoubleCheckLazyInitialisationWithAlias {

        @Test
        public void findAssignmentGuardForAliasedValidIntegerWithDefaultDcli() {
            final Reason r = new Reason(de.htwg_konstanz.jia.lazyinitialisation.doublecheck.AliasedIntegerWithDefault.class)
                    .forMethod("getSomeNumber", Type.INT_TYPE).andVariable("someNumber");
            assertThat(r.numberOfRelevantJumpInstructions(), is(2));
            assertThat(r.block(1), covers(r.relevantJumpInstructions()));
            assertTrue(checksAgainstAppropriateComparativeValue(r));
        }

    } // ValidDoubleCheckLazyInitialisationWithAlias



    // Ueberpruefung der Sprunganweisung
    
    static final class AssignmentGuardChecker {
        private String variableName;
        private ConvenienceClassNode classNode;
        private Set<UnknownTypeValue> possibleInitialValuesForVariable;
        
        private AssignmentGuardChecker(final String theVariableName, final ConvenienceClassNode theClassNode) {
            variableName = theVariableName;
            classNode = theClassNode;
        }

        public static AssignmentGuardChecker newInstance(final String variableName, final ConvenienceClassNode classNode) {
            notEmpty(variableName, "Argument 'variableName' must not be empty!");
            notNull(classNode, "Argument 'classNode' must not be null!");
            return new AssignmentGuardChecker(variableName, classNode);
        }

        
    } // class AssignmentGuardChecker

    public static boolean checksAgainstAppropriateComparativeValue(final Reason r) {
        boolean result = false;
        final Set<UnknownTypeValue> possibleInitialValuesForVariable = r.initialValues();
        for (final ControlFlowBlock cfb : r.blocks()) {
            final List<AbstractInsnNode> blockInstructions = cfb.getBlockInstructions();
            for (final JumpInsn relevantJumpInsn : cfb.getEffectiveJumpInstructionsForVariable(r.variableName())) {
                final int indexOfPredecessorInstruction = relevantJumpInsn.getIndexWithinBlock() - 1;
                final AbstractInsnNode predecessorInstruction = cfb.getBlockInstructionForIndex(indexOfPredecessorInstruction);

                if (isOneValueJumpInstruction(relevantJumpInsn)) {
                    if (checksAgainstZero(relevantJumpInsn)) {
                        if (isGetfieldForVariable(predecessorInstruction, r.variableName())) {
                            if (isZeroOnlyPossibleInitialValueForVariable(possibleInitialValuesForVariable)) {
                                // passt
                                System.out.println("Passt.");
                                result = true;
                            } else {
                                System.out.println("Nicht korrekt verzoegert initialisiert.");
                                result = false;
                            }
                        } else if (isLoadInstructionForAlias(r.variableName(), cfb, predecessorInstruction)) {
                            // passt
                            System.out.println("Passt.");
                            result = true;
                        } else if (isComparisonInsn(predecessorInstruction)) {
                            final int indexOfPreComparisonInsn = indexOfPredecessorInstruction - 1;
                            final AbstractInsnNode predecessorOfComparisonInsn = blockInstructions.get(indexOfPreComparisonInsn);
                            if (isGetfieldForVariable(predecessorOfComparisonInsn, r.variableName())) {
                                result = foo(indexOfPreComparisonInsn, blockInstructions, possibleInitialValuesForVariable);
                            } else if (isLoadInstructionForAlias(r.variableName(), cfb, predecessorOfComparisonInsn)) {
                                result = bar(indexOfPreComparisonInsn, blockInstructions, possibleInitialValuesForVariable);
                            }
                        } else if (checksAgainstOtherObject(relevantJumpInsn, blockInstructions, r.variableName())) {
                            if (isOtherObjectAnInitialValue(relevantJumpInsn, blockInstructions)) {
                                System.out.println("Passt.");
                                result = true;
                            } else {
                                // nicht korrekt verzoegert initialisiert
                                System.out.println("Nicht korrekt verzoegert initialisiert.");
                                result = false;
                            }
                        }
                    } else if (checksAgainstNull(relevantJumpInsn)) {
                        // TODO Was passiert hier?
                    } else if (checksAgainstNonNull(relevantJumpInsn)) {
                        if (!possibleInitialValuesForVariable.contains(DefaultUnknownTypeValue.getInstanceForNull())) {
                            // nicht korrekt verzoegert initialisiert
                            System.out.println("Nicht korrekt verzoegert initialisiert.");
                            result = false;
                        } else if (isGetfieldForVariable(predecessorInstruction, r.variableName())) {
                            final UnknownTypeValue nullValue = DefaultUnknownTypeValue.getInstanceForNull();
                            if (possibleInitialValuesForVariable.contains(nullValue)) {
                                // passt
                                System.out.println("Passt.");
                                result = true;
                            } else {
                                // nicht korrekt verzoegert initialisiert
                                System.out.println("Nicht korrekt verzoegert initialisiert.");
                                result = false;
                            }
                        } else if (isLoadInstructionForAlias(r.variableName(), cfb, predecessorInstruction)) {
                            // passt
                            System.out.println("Passt.");
                            result = true;
                        } else if (checksAgainstOtherObject(relevantJumpInsn, blockInstructions, r.variableName())) {
                            // TODO Block implementieren.
                        }
                    }
                } else if (isTwoValuesJumpInstruction(relevantJumpInsn)) {
                    if (isGetfieldForVariable(predecessorInstruction, r.variableName())) {
                        result = foo(indexOfPredecessorInstruction, blockInstructions, possibleInitialValuesForVariable);
                    } else if (isLoadInstructionForAlias(r.variableName(), cfb, predecessorInstruction)) {
                        result = bar(indexOfPredecessorInstruction, blockInstructions, possibleInitialValuesForVariable);
                    }
                }
            }
        }
        return result;
    }

    private static boolean isOtherObjectAnInitialValue(JumpInsn jumpInsn,
            List<AbstractInsnNode> blockInstructions) {
        final int indexWithinBlock = jumpInsn.getIndexWithinBlock();
        final AbstractInsnNode predecessorInsn = blockInstructions.get(indexWithinBlock - 2);
        final boolean result;
        if (ACONST_NULL == predecessorInsn.getOpcode()) {
            result = true;
        } else {
            result = false;
            // TODO Auto-generated method stub
        }
        return result;
    }

    private static boolean checksAgainstOtherObject(final JumpInsn jumpInsn,
            final List<AbstractInsnNode> blockInstructions, String variableName) {
        final int indexWithinBlock = jumpInsn.getIndexWithinBlock();
        final AbstractInsnNode possibleEqualsInsn = blockInstructions.get(indexWithinBlock - 1);
        boolean result = false;
        if (isEqualsInstruction(possibleEqualsInsn)) {
            final AbstractInsnNode possibleGetfieldInsnForVariable = blockInstructions.get(indexWithinBlock - 3);
            result = isGetfieldForVariable(possibleGetfieldInsnForVariable, variableName);
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

    private static boolean isZeroOnlyPossibleInitialValueForVariable(
            final Set<UnknownTypeValue> possibleInitialValuesForVariable) {
        boolean result = true;
        final Iterator<UnknownTypeValue> i = possibleInitialValuesForVariable.iterator();
        while (result && i.hasNext()) {
            final UnknownTypeValue u = i.next();
            result = u.isZero();
        }
        return result;
    }

    private static boolean bar(final int indexOfPreComparisonInsn,
            final List<AbstractInsnNode> blockInstructions,
            final Set<UnknownTypeValue> possibleInitialValuesForVariable) {
        final boolean result;
        final int indexOfLoadInsnPredecessor = indexOfPreComparisonInsn - 1;
        final AbstractInsnNode predecessorOfLoadInsn = blockInstructions.get(indexOfLoadInsnPredecessor);
        final UnknownTypeValue comparativeValue = getComparativeValue(predecessorOfLoadInsn);
        if (possibleInitialValuesForVariable.contains(comparativeValue)) {
            // passt
            System.out.println("Passt.");
            result = true;
        } else {
            // nicht korrekt verzoegert initialisiert
            System.out.println("Nicht korrekt verzoegert initialisiert.");
            result = false;
        }
        return result;
    }

    private static boolean foo(final int indexOfPreComparisonInsn,
            final List<AbstractInsnNode> blockInstructions,
            final Set<UnknownTypeValue> possibleInitialValuesForVariable) {
        final boolean result;
        final int indexOfGetfieldPredecessorInsn = indexOfPreComparisonInsn - 2;
        final AbstractInsnNode predecessorOfGetfieldInsn = blockInstructions.get(indexOfGetfieldPredecessorInsn);
        final UnknownTypeValue comparativeValue = getComparativeValue(predecessorOfGetfieldInsn);
        if (possibleInitialValuesForVariable.contains(comparativeValue)) {
            // passt
            System.out.println("Passt.");
            result = true;
        } else {
            // nicht korrekt verzoegert initialisiert
            System.out.println("Nicht korrekt verzoegert initialisiert.");
            result = false;
        }
        return result;
    }

    private static boolean isGetfieldForVariable(final AbstractInsnNode insn, final String variableName) {
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

    private static boolean isLoadInstructionForAlias(final String variableName,
            final ControlFlowBlock blockWithJumpInsn,
            final AbstractInsnNode insn) {
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

    private static UnknownTypeValue getComparativeValue(final AbstractInsnNode insn) {
        UnknownTypeValue result = null;
        if (AbstractInsnNode.INSN == insn.getType()) {
            final Opcode opcode = Opcode.forInt(insn.getOpcode());
            result = opcode.stackValue();
        } else if (AbstractInsnNode.LDC_INSN == insn.getType()) {
            final LdcInsnNode ldcInsn = (LdcInsnNode) insn;
            result = DefaultUnknownTypeValue.getInstance(ldcInsn.cst);
        } else if (AbstractInsnNode.INT_INSN == insn.getType()) {
            final IntInsnNode intInsnNode = (IntInsnNode) insn;
            result = DefaultUnknownTypeValue.getInstance(intInsnNode.operand);
        }
        return result;
    }

    private static boolean isOneValueJumpInstruction(final JumpInsn jumpInstruction) {
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

    private static boolean checksAgainstZero(final JumpInsn jumpInstruction) {
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

    private static boolean checksAgainstNull(final JumpInsn jumpInstruction) {
        return IFNULL == getOpcode(jumpInstruction);
    }

    private static boolean checksAgainstNonNull(final JumpInsn jumpInstruction) {
        return IFNONNULL == getOpcode(jumpInstruction);
    }

    private static boolean isTwoValuesJumpInstruction(final JumpInsn jumpInstruction) {
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
