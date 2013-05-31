/**
 * 
 */
package org.mutabilitydetector.checkers.settermethod;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mutabilitydetector.checkers.settermethod.AssignmentGuardFinderTest.ContainsMatcher.containsAssignmentGuardFor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mutabilitydetector.benchmarks.settermethod.singlecheck.WithAlias;
import org.mutabilitydetector.benchmarks.settermethod.singlecheck.WithoutAlias;
import org.mutabilitydetector.benchmarks.settermethod.singlecheck.WithoutAlias.WithJvmInitialValue.CustomObjectInvalid.SomeObject;
import org.mutabilitydetector.checkers.settermethod.CandidatesInitialisersMapping.Initialisers;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldNode;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 20.02.2013
 */
@RunWith(Enclosed.class)
public final class AssignmentGuardFinderTest {

    @NotThreadSafe
    private static final class Reason {

        private final EnhancedClassNode ccn;
        private final List<ControlFlowBlock> cfbs;
        private String variableName;
        private final Set<UnknownTypeValue> possibleInitialValuesForVariable;

        public Reason(final Class<?> klasse) {
            ccn = createConvenienceClassNodeFor(klasse);
            cfbs = new ArrayList<ControlFlowBlock>();
            variableName = "";
            possibleInitialValuesForVariable = new HashSet<UnknownTypeValue>();
        }

        private static EnhancedClassNode createConvenienceClassNodeFor(final Class<?> klasse) {
            final ClassNodeFactory factory = ClassNodeFactory.getInstance();
            return factory.getConvenienceClassNodeFor(klasse);
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
            final VariableInitialisersMappingFactory factory = VariableInitialisersMappingFactory.getInstance();
            final CandidatesInitialisersMapping candidatesInitialisersMapping = factory
                    .getVariableInitialisersAssociationFor(ccn);
            final FieldNode variable = ccn.findVariableWithName(variableName);
            final Initialisers setters = candidatesInitialisersMapping.getInitialisersFor(variable);
            final Finder<Set<UnknownTypeValue>> f = InitialValueFinder.newInstance(variable, setters, ccn);
            possibleInitialValuesForVariable.addAll(f.find());
        }

        public String variableName() {
            return variableName;
        }

        public ControlFlowBlock block(final int theBlockNumber) {
            return cfbs.get(theBlockNumber);
        }

        public int numberOfAssignmentGuards() {
            final Set<JumpInsn> assignmentGuards = new HashSet<JumpInsn>();
            for (final ControlFlowBlock cfb : cfbs) {
                final Finder<JumpInsn> f = AssignmentGuardFinder.newInstance(variableName, cfb);
                final JumpInsn supposedAssignmentGuard = f.find(); 
                if (supposedAssignmentGuard.isAssignmentGuard()) {
                    assignmentGuards.add(supposedAssignmentGuard);
                }
            }
            return assignmentGuards.size();
        }

    } // class Reason


    @ThreadSafe
    static final class ContainsMatcher extends TypeSafeMatcher<ControlFlowBlock> {

        private final String variableName;

        private ContainsMatcher(final String theVariableName) {
            variableName = notEmpty(theVariableName);
        }

        public static ContainsMatcher containsAssignmentGuardFor(final String variableName) {
            return new ContainsMatcher(variableName);
        }

        @Override
        public void describeTo(final Description description) {
            description.appendText("block contains assignment guard for ");
            description.appendValue(variableName);            
        }

        @Override
        protected boolean matchesSafely(final ControlFlowBlock controlFlowBlock) {
            final Finder<JumpInsn> f = AssignmentGuardFinder.newInstance(variableName, controlFlowBlock);
            final JumpInsn supposedAssignmentGuard = f.find();
            return supposedAssignmentGuard.isAssignmentGuard();
        }

    } // class ContainsMatcher


    public static final class ValidSingleCheckLazyInitialisationWithoutAlias {

        @Test
        public void integerWithJvmInitial() {
            final Class<?> klasse = WithoutAlias.WithJvmInitialValue.IntegerValid.class;
            final Reason r = new Reason(klasse).forMethod("hashCode", Type.INT_TYPE).andVariable("hash");
            assertThat(r.numberOfAssignmentGuards(), is(1));
            assertThat(r.block(0), containsAssignmentGuardFor(r.variableName()));
        }

        @Test
        public void floatWithJvmInitialValue() {
            final Class<?> klasse = WithoutAlias.WithJvmInitialValue.FloatValid.class;
            final Reason r = new Reason(klasse).forMethod("hashCodeFloat", Type.FLOAT_TYPE).andVariable("hash");
            assertThat(r.numberOfAssignmentGuards(), is(1));
            assertThat(r.block(0), containsAssignmentGuardFor(r.variableName()));
        }

        @Test
        public void charWithJvmInitial() {
            final Class<?> klasse = WithoutAlias.WithJvmInitialValue.CharValid.class;
            final Reason r = new Reason(klasse).forMethod("hashCodeChar", Type.CHAR_TYPE).andVariable("hash");
            assertThat(r.numberOfAssignmentGuards(), is(1));
            assertThat(r.block(0), containsAssignmentGuardFor(r.variableName()));
        }

        @Test
        public void objectWithJvmInitialValue() {
            final Reason r = new Reason(WithoutAlias.WithJvmInitialValue.ObjectValid.class).forMethod("hashCodeObject",
                    Type.getType(Object.class)).andVariable("hash");
            assertThat(r.numberOfAssignmentGuards(), is(1));
            assertThat(r.block(0), containsAssignmentGuardFor(r.variableName()));
        }

        @Test
        public void integerWithCustomInitialValue() {
            final Class<?> klasse = WithoutAlias.WithCustomInitialValue.IntegerValid.class;
            final Reason r = new Reason(klasse).forMethod("hashCode", Type.INT_TYPE).andVariable("hash");
            assertThat(r.numberOfAssignmentGuards(), is(1));
            assertThat(r.block(0), containsAssignmentGuardFor(r.variableName()));
        }

        @Test
        public void floatWithCustomInitialValue() {
            final Class<?> klasse = WithoutAlias.WithCustomInitialValue.FloatValid.class;
            final Reason r = new Reason(klasse).forMethod("hashCodeFloat", Type.FLOAT_TYPE).andVariable("hash");
            assertThat(r.numberOfAssignmentGuards(), is(1));
            assertThat(r.block(0), containsAssignmentGuardFor(r.variableName()));
        }

        @Test
        public void customObjectWithJvmInitialValue() {
            final Reason r = new Reason(WithoutAlias.WithJvmInitialValue.CustomObjectInvalid.class).forMethod(
                    "hashCodeSomeObject", Type.getType(SomeObject.class)).andVariable("someObject");
            assertThat(r.numberOfAssignmentGuards(), is(1));
            assertThat(r.block(0), containsAssignmentGuardFor(r.variableName()));
        }

    } // class TestsForValidSingleCheckLazyInitialisationWithoutAlias


    public static final class InvalidSingleCheckLazyInitialisationWithoutAlias {

        @Test
        public void charWithJvmInitialValue() {
            final Class<?> klasse = WithoutAlias.WithJvmInitialValue.CharInvalid.class;
            final Reason r = new Reason(klasse).forMethod("hashCodeChar", Type.CHAR_TYPE).andVariable("hash");
            assertThat(r.numberOfAssignmentGuards(), is(0));
        }

        @Test
        public void integerWithJvmInitialValue() {
            final Class<?> klasse = WithoutAlias.WithJvmInitialValue.IntegerInvalid.class;
            final Reason r = new Reason(klasse).forMethod("hashCode", Type.INT_TYPE).andVariable("hash");
            assertThat(r.numberOfAssignmentGuards(), is(1));
            assertThat(r.block(0), containsAssignmentGuardFor(r.variableName()));
        }

        @Test
        public void floatWithJvmInitialValue() {
            final Class<?> klasse = WithoutAlias.WithJvmInitialValue.FloatInvalid.class;
            final Reason r = new Reason(klasse).forMethod("hashCodeFloat", Type.FLOAT_TYPE).andVariable("hash");
            assertThat(r.numberOfAssignmentGuards(), is(1));
            assertThat(r.block(0), containsAssignmentGuardFor(r.variableName()));
        }

        @Test
        public void objectWithJvmInitialValue() {
            final Reason r = new Reason(WithoutAlias.WithJvmInitialValue.ObjectInvalid.class).forMethod(
                    "hashCodeObject", Type.getType(Object.class)).andVariable("hash");
            // FIXME
            assertThat(r.numberOfAssignmentGuards(), is(0));
        }

        @Test
        public void stringWithJvmInitialValue() {
            final Reason r = new Reason(WithoutAlias.WithJvmInitialValue.StringInvalid.class).forMethod(
                    "hashCodeString", Type.getType(String.class)).andVariable("hash");
            assertThat(r.numberOfAssignmentGuards(), is(0));
        }

        @Test
        public void floatWithMultipleCustomInitialValues() {
            final Class<?> klasse = WithoutAlias.WithCustomInitialValue.FloatInvalidWithMultipleInitialValues.class;
            final Reason r = new Reason(klasse).forMethod("hashCodeFloat", Type.FLOAT_TYPE).andVariable("hash");
            assertThat(r.numberOfAssignmentGuards(), is(1));
            assertThat(r.block(0), containsAssignmentGuardFor(r.variableName()));
        }

        @Test
        public void integerWithCustomInitialValue() {
            final Class<?> klasse = WithoutAlias.WithCustomInitialValue.IntegerInvalid.class;
            final Reason r = new Reason(klasse).forMethod("hashCode", Type.INT_TYPE).andVariable("hash");
            assertThat(r.numberOfAssignmentGuards(), is(1));
            assertThat(r.block(0), containsAssignmentGuardFor(r.variableName()));
        }

        @Test
        public void stringWithCustomInitialValue() {
            final Reason r = new Reason(WithoutAlias.WithCustomInitialValue.StringInvalid.class).forMethod(
                    "hashCodeString", Type.getType(String.class)).andVariable("hash");
            assertThat(r.numberOfAssignmentGuards(), is(0));
        }

    } // class InvalidSingleCheckLazyInitialisationWithoutAlias


    public static final class ValidSingleCheckLazyInitialisationWithAlias {

        @Test
        public void byteWithJvmInitialValue() {
            final Class<?> klasse = WithAlias.WithJvmInitialValue.ByteValid.class;
            final Reason r = new Reason(klasse).forMethod("hashCodeByte", Type.BYTE_TYPE).andVariable("hash");
            assertThat(r.numberOfAssignmentGuards(), is(1));
            assertThat(r.block(1), containsAssignmentGuardFor(r.variableName()));
        }

        @Test
        public void shortWithJvmInitialValue() {
            final Class<?> klasse = WithAlias.WithJvmInitialValue.ShortValid.class;
            final Reason r = new Reason(klasse).forMethod("hashCodeShort", Type.SHORT_TYPE).andVariable("hash");
            assertThat(r.numberOfAssignmentGuards(), is(1));
            assertThat(r.block(1), containsAssignmentGuardFor(r.variableName()));
        }

        @Test
        public void floatWithJvmInitialValue() {
            final Class<?> klasse = WithAlias.WithJvmInitialValue.FloatValid.class;
            final Reason r = new Reason(klasse).forMethod("hashCodeFloat", Type.FLOAT_TYPE).andVariable("hash");
            assertThat(r.numberOfAssignmentGuards(), is(1));
            assertThat(r.block(1), containsAssignmentGuardFor(r.variableName()));
        }

        @Test
        public void javaLangString() {
            final Class<?> klasse = String.class;
            final Reason r = new Reason(klasse).forMethod("hashCode", Type.INT_TYPE).andVariable("hash");
            assertThat(r.numberOfAssignmentGuards(), is(1));
        }

        @Test
        public void stringWithJvmInitialValue() {
            final Class<?> klasse = WithAlias.WithJvmInitialValue.StringValid.class;
            final Reason r = new Reason(klasse).forMethod("hashCodeString", Type.getType(String.class)).andVariable("hash");
            assertThat(r.numberOfAssignmentGuards(), is(1));
            assertThat(r.block(1), containsAssignmentGuardFor(r.variableName()));
        }

        @Test
        public void stringWithCustomInitialValue() {
            final Class<?> klasse = WithAlias.WithCustomInitialValue.StringValid.class;
            final Reason r = new Reason(klasse).forMethod("hashCodeString", Type.getType(String.class)).andVariable("hash");
            assertThat(r.numberOfAssignmentGuards(), is(1));
            assertThat(r.block(1), containsAssignmentGuardFor(r.variableName()));
        }

        @Test
        public void integerWithCustomInitialValue() {
            final Class<?> klasse = WithAlias.WithCustomInitialValue.IntegerValid.class;
            final Reason r = new Reason(klasse).forMethod("getMessageLength", Type.INT_TYPE).andVariable("cachedValue");
            assertThat(r.numberOfAssignmentGuards(), is(1));
            assertThat(r.block(1), containsAssignmentGuardFor(r.variableName()));
        }

    } // TestsForValidSingleCheckLazyInitialisationWithAlias


    public static final class ValidDoubleCheckLazyInitialisationWithAlias {

        @Test
        public void findAssignmentGuardForAliasedValidIntegerWithDefaultDcli() {
            final Reason r = new Reason(
                    org.mutabilitydetector.benchmarks.settermethod.doublecheck.AliasedIntegerWithDefault.class)
                    .forMethod("getSomeNumber", Type.INT_TYPE).andVariable("someNumber");
            assertThat(r.numberOfAssignmentGuards(), is(1));
            assertThat(r.block(1), containsAssignmentGuardFor(r.variableName()));
        }

    } // ValidDoubleCheckLazyInitialisationWithAlias

}
