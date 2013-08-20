/*
 *    Copyright (c) 2008-2013 Graham Allan, Juergen Fickel
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package org.mutabilitydetector.benchmarks.settermethod;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mutabilitydetector.TestUtil.runChecker;
import static org.mutabilitydetector.checkers.CheckerRunner.ExceptionPolicy.FAIL_FAST;
import static org.mutabilitydetector.unittesting.AllowedReason.provided;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areEffectivelyImmutable;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areNotImmutable;

import java.io.IOException;
import java.math.BigDecimal;

import javax.annotation.concurrent.Immutable;

import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.MethodRule;
import org.junit.runner.RunWith;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.ConfigurationBuilder;
import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.TestUtil;
import org.mutabilitydetector.benchmarks.ImmutableExample;
import org.mutabilitydetector.benchmarks.settermethod.SetsFieldsOfDifferentTypes.SetsBoolean;
import org.mutabilitydetector.benchmarks.settermethod.SetsFieldsOfDifferentTypes.SetsByte;
import org.mutabilitydetector.benchmarks.settermethod.SetsFieldsOfDifferentTypes.SetsChar;
import org.mutabilitydetector.benchmarks.settermethod.SetsFieldsOfDifferentTypes.SetsDouble;
import org.mutabilitydetector.benchmarks.settermethod.SetsFieldsOfDifferentTypes.SetsFloat;
import org.mutabilitydetector.benchmarks.settermethod.SetsFieldsOfDifferentTypes.SetsInt;
import org.mutabilitydetector.benchmarks.settermethod.SetsFieldsOfDifferentTypes.SetsLong;
import org.mutabilitydetector.benchmarks.settermethod.SetsFieldsOfDifferentTypes.SetsObjectArray;
import org.mutabilitydetector.benchmarks.settermethod.SetsFieldsOfDifferentTypes.SetsObjectArrayArray;
import org.mutabilitydetector.benchmarks.settermethod.SetsFieldsOfDifferentTypes.SetsReference;
import org.mutabilitydetector.benchmarks.settermethod.SetsFieldsOfDifferentTypes.SetsShort;
import org.mutabilitydetector.benchmarks.settermethod.doublecheck.WithAlias.WithCustomInitialValue.MessageHolder;
import org.mutabilitydetector.benchmarks.settermethod.doublecheck.WithAlias.WithCustomInitialValue.MessageHolderWithWrongAssignmentGuard;
import org.mutabilitydetector.benchmarks.settermethod.singlecheck.WithAlias;
import org.mutabilitydetector.benchmarks.settermethod.singlecheck.WithoutAlias;
import org.mutabilitydetector.benchmarks.types.EnumType;
import org.mutabilitydetector.checkers.AsmMutabilityChecker;
import org.mutabilitydetector.checkers.CheckerRunner;
import org.mutabilitydetector.checkers.OldSetterMethodChecker;
import org.mutabilitydetector.checkers.info.PrivateMethodInvocationInformation;
import org.mutabilitydetector.checkers.info.SessionCheckerRunner;
import org.mutabilitydetector.checkers.settermethod.SetterMethodChecker;
import org.mutabilitydetector.junit.FalsePositive;
import org.mutabilitydetector.junit.IncorrectAnalysisRule;
import org.mutabilitydetector.locations.ClassName;
import org.mutabilitydetector.locations.CodeLocation;
import org.mutabilitydetector.locations.Dotted;
import org.mutabilitydetector.locations.FieldLocation;
import org.mutabilitydetector.unittesting.MutabilityAsserter;
import org.objectweb.asm.ClassReader;

@RunWith(Enclosed.class)
public final class SetterMethodCheckerTest {
    
    @Immutable
    static final class IsImmutableMatcher extends TypeSafeMatcher<Class<?>> {

        private static final IsImmutableMatcher INSTANCE = new IsImmutableMatcher();

        private IsImmutableMatcher() {
            super();
        }

        public static IsImmutableMatcher isImmutable() {
            return INSTANCE;
        }

        @Override
        public void describeTo(final Description description) {
            description.appendText("is immutable.");
        }

        @Override
        protected boolean matchesSafely(final Class<?> klasse) {
            final AsmMutabilityChecker checker = createSetterMethodChecker(klasse);
            final IsImmutable checkerResult = checker.result();
            return IsImmutable.IMMUTABLE == checkerResult;
        }

        private AsmMutabilityChecker createSetterMethodChecker(final Class<?> eineKlasse) {
            
            final ClassName dotted = Dotted.fromClass(checkNotNull(eineKlasse));
            final ClassReader cr = tryToCreateClassReaderFor(dotted.asString());
            final AsmMutabilityChecker result = SetterMethodChecker.newInstance();
            cr.accept(result, 0);
            return result;
        }

        private static ClassReader tryToCreateClassReaderFor(final String dottedClassName) {
            try {
                return new ClassReader(dottedClassName);
            } catch (final IOException e) {
                final String msg = String.format("Unable to create ClassReader for '%s'.", dottedClassName);
                throw new IllegalStateException(msg, e);
            }
        }

    } // class IsImmutableMatcher


    @Immutable
    static final class IsMutableMatcher extends TypeSafeMatcher<Class<?>> {

        private static final IsMutableMatcher INSTANCE = new IsMutableMatcher();

        private IsMutableMatcher() {
            super();
        }

        public static IsMutableMatcher isMutable() {
            return INSTANCE;
        }

        @Override
        public void describeTo(final Description description) {
            description.appendText("is mutable.");
        }

        @Override
        protected boolean matchesSafely(final Class<?> klasse) {
            final AsmMutabilityChecker checker = createSetterMethodChecker(klasse);
            return IsImmutable.NOT_IMMUTABLE == checker.result();
        }

        private AsmMutabilityChecker createSetterMethodChecker(final Class<?> eineKlasse) {
            final ClassName dotted = Dotted.fromClass(checkNotNull(eineKlasse));
            final ClassReader cr = tryToCreateClassReaderFor(dotted.asString());
            final AsmMutabilityChecker result = SetterMethodChecker.newInstance();
            cr.accept(result, 0);
            return result;
        }

        private static ClassReader tryToCreateClassReaderFor(final String dottedClassName) {
            try {
                return new ClassReader(dottedClassName);
            } catch (final IOException e) {
                final String msg = String.format("Unable to create ClassReader for '%s'.", dottedClassName);
                throw new IllegalStateException(msg, e);
            }
        }
        
    } // class IsImmutableMatcher

    private static final MutabilityAsserter asserterWithLazyInitialisation = MutabilityAsserter.configured(new ConfigurationBuilder() {
        @Override public void configure() {
            useAdvancedReassignedFieldAlgorithm();
        }
    });

    public static final class ValidSingleCheckLazyInitialisationWithoutAlias {
        
        @Test
        public void booleanFlagRendersImmutable() {
            final Class<?> klasse = WithoutAlias.WithJvmInitialValue.BooleanFlag.class;
            asserterWithLazyInitialisation.assertImmutable(klasse);
        }

        @Test
        public void booleanFlagWithFalseAssignmentGuardRendersNotImmutable() {
            final Class<?> klasse = WithoutAlias.WithJvmInitialValue.BooleanFlagWithFalseAssignmentGuard.class;
            asserterWithLazyInitialisation.assertInstancesOf(klasse, areNotImmutable());
        }

        @Test
        public void verifyIntegerWithJvmInitial() {
            final Class<?> klasse = WithoutAlias.WithJvmInitialValue.IntegerValid.class;
            asserterWithLazyInitialisation.assertImmutable(klasse);
        }

        @Test
        public void integerWithJvmInitial() {
            final Class<?> klasse = WithoutAlias.WithJvmInitialValue.IntegerValid.class;
            asserterWithLazyInitialisation.assertImmutable(klasse);
        }

        @Test
        public void floatWithJvmInitialValue() {
            final Class<?> klasse = WithoutAlias.WithJvmInitialValue.FloatValid.class;
            asserterWithLazyInitialisation.assertImmutable(klasse);
        }

        @Test
        public void charWithJvmInitial() {
            final Class<?> klasse = WithoutAlias.WithJvmInitialValue.CharValid.class;
            asserterWithLazyInitialisation.assertImmutable(klasse);
        }

        @Test
        public void objectWithJvmInitialValue() {
            final Class<?> klasse = WithoutAlias.WithJvmInitialValue.ObjectValid.class;
            asserterWithLazyInitialisation.assertInstancesOf(klasse, areImmutable(), provided(Object.class).isAlsoImmutable());
        }

        @Test
        public void integerWithCustomInitialValue() {
            final Class<?> klasse = WithoutAlias.WithCustomInitialValue.IntegerValid.class;
            asserterWithLazyInitialisation.assertImmutable(klasse);
        }

        @Test
        public void floatWithCustomInitialValue() {
            final Class<?> klasse = WithoutAlias.WithCustomInitialValue.FloatValid.class;
            asserterWithLazyInitialisation.assertImmutable(klasse);
        }

        @Test
        public void customObjectWithJvmInitialValue() {
            final Class<?> klasse = WithoutAlias.WithJvmInitialValue.CustomObjectInvalid.class;
            asserterWithLazyInitialisation.assertInstancesOf(klasse, areNotImmutable());
        }

    } // class TestsForValidSingleCheckLazyInitialisationWithoutAlias


    public static final class InvalidSingleCheckLazyInitialisationWithoutAlias {

        @Test
        public void charWithJvmInitialValue() {
            final Class<?> klasse = WithoutAlias.WithJvmInitialValue.CharInvalid.class;
            asserterWithLazyInitialisation.assertInstancesOf(klasse, areNotImmutable());
        }

        @Test
        public void integerWithJvmInitialValue() {
            final Class<?> klasse = WithoutAlias.WithJvmInitialValue.IntegerInvalid.class;
            asserterWithLazyInitialisation.assertInstancesOf(klasse, areNotImmutable());
        }

        @Test
        public void floatWithJvmInitialValue() {
            final Class<?> klasse = WithoutAlias.WithJvmInitialValue.FloatInvalid.class;
            asserterWithLazyInitialisation.assertInstancesOf(klasse, areNotImmutable());
        }

        @Test
        public void objectWithJvmInitialValue() {
            final Class<?> klasse = WithoutAlias.WithJvmInitialValue.ObjectInvalid.class;
            asserterWithLazyInitialisation.assertInstancesOf(klasse, areNotImmutable());
        }

        @Test
        public void stringWithJvmInitialValue() {
            final Class<?> klasse = WithoutAlias.WithJvmInitialValue.StringInvalid.class;
            asserterWithLazyInitialisation.assertInstancesOf(klasse, areNotImmutable());
        }

        @Test
        public void floatWithMultipleCustomInitialValues() {
            final Class<?> klasse = WithoutAlias.WithCustomInitialValue.FloatInvalidWithMultipleInitialValues.class;
            asserterWithLazyInitialisation.assertInstancesOf(klasse, areNotImmutable());
        }

        @Test
        public void integerWithCustomInitialValue() {
            final Class<?> klasse = WithoutAlias.WithCustomInitialValue.IntegerInvalid.class;
            asserterWithLazyInitialisation.assertInstancesOf(klasse, areNotImmutable());
        }

        @Test
        public void stringWithCustomInitialValue() {
            final Class<?> klasse = WithoutAlias.WithCustomInitialValue.StringInvalid.class;
            asserterWithLazyInitialisation.assertInstancesOf(klasse, areNotImmutable());
        }

        @Test
        public void integerWithNonCandidateVariableRendersEffectivelyImmutable() {
            final Class<?> klasse = WithoutAlias.WithCustomInitialValue.IntegerWithNonCandidateVariable.class;
            asserterWithLazyInitialisation.assertInstancesOf(klasse, areEffectivelyImmutable());
        }

        @Test
        public void integerWithInvalidValueCalculationMethodRendersMutable() {
            final Class<?> klasse = WithoutAlias.WithJvmInitialValue.IntegerWithInvalidValueCalculationMethod.class;
            asserterWithLazyInitialisation.assertInstancesOf(klasse, areNotImmutable());
        }

        @Test
        public void stringWithInvalidValueCalculationMethodRendersMutable() {
            final Class<?> klasse = WithoutAlias.WithJvmInitialValue.StringWithInvalidValueCalculationMethod.class;
            asserterWithLazyInitialisation.assertInstancesOf(klasse, areNotImmutable());
        }

    } // class InvalidSingleCheckLazyInitialisationWithoutAlias


    public static final class ValidSingleCheckLazyInitialisationWithAlias {

        @Test
        public void byteWithJvmInitialValue() {
            final Class<?> klasse = WithAlias.WithJvmInitialValue.ByteValid.class;
            asserterWithLazyInitialisation.assertImmutable(klasse);
        }

        @Test
        public void shortWithJvmInitialValue() {
            final Class<?> klasse = WithAlias.WithJvmInitialValue.ShortValid.class;
            asserterWithLazyInitialisation.assertImmutable(klasse);
        }

        @Test
        public void floatWithJvmInitialValue() {
            final Class<?> klasse = WithAlias.WithJvmInitialValue.FloatValid.class;
            asserterWithLazyInitialisation.assertImmutable(klasse);
        }

        @Test
        public void javaLangString() {
            final Class<?> klasse = String.class;
            asserterWithLazyInitialisation.assertInstancesOf(klasse, areNotImmutable());
        }

        @Test
        public void stringWithJvmInitialValue() {
            final Class<?> klasse = WithAlias.WithJvmInitialValue.StringValid.class;
            asserterWithLazyInitialisation.assertInstancesOf(klasse, areImmutable(), provided(String.class).isAlsoImmutable());
        }

        @Test
        public void stringWithCustomInitialValue() {
            final Class<?> klasse = WithAlias.WithCustomInitialValue.StringValid.class;
            asserterWithLazyInitialisation.assertInstancesOf(klasse, areImmutable(), provided(String.class).isAlsoImmutable());
        }

        @Test
        public void integerWithCustomInitialValue() {
            final Class<?> klasse = WithAlias.WithCustomInitialValue.IntegerValid.class;
            asserterWithLazyInitialisation.assertInstancesOf(klasse, areImmutable(), provided(String.class).isAlsoImmutable());
        }

    } // TestsForValidSingleCheckLazyInitialisationWithAlias


    public static final class ValidDoubleCheckLazyInitialisationWithAlias {

        @Test
        public void aliasedValidIntegerWithJvmInitialValue() {
            final Class<?> klasse = org.mutabilitydetector.benchmarks.settermethod.doublecheck.AliasedIntegerWithDefault.class;
            asserterWithLazyInitialisation.assertImmutable(klasse);
        }

        @Test
        public void messageHolderRendersImmutable() {
            final Class<?> klasse = MessageHolder.class;
            asserterWithLazyInitialisation.assertInstancesOf(klasse, areImmutable(), provided(String.class).isAlsoImmutable());
        }

        @Test
        public void messageHolderWithWrongAssignmentGuardRendersMutable() {
            final Class<?> klasse = MessageHolderWithWrongAssignmentGuard.class;
            asserterWithLazyInitialisation.assertInstancesOf(klasse, areNotImmutable(), provided(String.class).isAlsoImmutable());
        }

    } // ValidDoubleCheckLazyInitialisationWithAlias


    @RunWith(Theories.class)
    public static final class OriginalTests {

        @Rule public MethodRule rule = new IncorrectAnalysisRule();
        private OldSetterMethodChecker checker;
        private CheckerRunner checkerRunner;
        private AnalysisSession analysisSession;
        private PrivateMethodInvocationInformation info;
        @DataPoints
        public static Class<?>[] classes = new Class[] { SetsBoolean.class, SetsByte.class, SetsChar.class,
                SetsDouble.class, SetsFloat.class, SetsInt.class, SetsLong.class, SetsObjectArray.class,
                SetsObjectArrayArray.class, SetsReference.class, SetsShort.class };

        @Before
        public void setUp() {
            checkerRunner = CheckerRunner.createWithCurrentClasspath(FAIL_FAST);
            analysisSession = TestUtil.testAnalysisSession();
            info = new PrivateMethodInvocationInformation(new SessionCheckerRunner(analysisSession, checkerRunner));
            checker = OldSetterMethodChecker.newSetterMethodChecker(info, TestUtil.testingVerifierFactory());
        }

        private AnalysisResult doCheck(Class<?> toCheck) {
            return TestUtil.runChecker(checker, toCheck);
        }

        @Test
        public void immutableExamplePassesCheck() throws Exception {
            assertThat(doCheck(ImmutableExample.class), areImmutable());
        }

        @Test
        public void mutableByHavingSetterMethodFailsCheck() throws Exception {
            assertThat(doCheck(MutableByHavingSetterMethod.class), areNotImmutable());
        }

        @Test
        public void integerClassPassesCheck() throws Exception {
            assertThat(doCheck(Integer.class), areImmutable());
        }

        @Test
        public void enumTypePassesCheck() throws Exception {
            assertThat(doCheck(EnumType.class), areImmutable());
        }

        @FalsePositive("Field [myField] can be reassigned within method [setPrivateFieldOnInstanceOfSelf]" + "Field [primitiveField] can be reassigned within method [setPrivateFieldOnInstanceOfSelf]")
        @Test
        public void settingFieldOfOtherInstanceDoesNotRenderClassMutable() throws Exception {
            assertThat(doCheck(ImmutableButSetsPrivateFieldOfInstanceOfSelf.class), areImmutable());
        }

        @Test
        public void settingFieldOfOtherInstanceAndThisInstanceRendersClassMutable() throws Exception {
            assertThat(doCheck(MutableBySettingFieldOnThisInstanceAndOtherInstance.class), areNotImmutable());
        }

        @Test
        public void fieldsSetInPrivateMethodCalledOnlyFromConstructorIsImmutable() {
            assertThat(doCheck(ImmutableUsingPrivateFieldSettingMethod.class), areImmutable());
        }

        @FalsePositive("Field [reassignable] can be reassigned within method [setFieldOnParameter]")
        @Test
        public void settingFieldOfObjectPassedAsParameterDoesNotRenderClassMutable() throws Exception {
            assertThat(doCheck(ImmutableButSetsFieldOfOtherClass.class), areImmutable());
        }

        @Test
        public void settingFieldOfMutableFieldRendersClassMutable() throws Exception {
            assertThat(doCheck(MutableBySettingFieldOfField.class), areNotImmutable());
        }

        @FalsePositive("Does not create any reasons.")
        @Test
        public void subclassOfSettingFieldOfMutableFieldRendersClassMutable() throws Exception {
            AnalysisResult result = doCheck(StillMutableSubclass.class);
        
            assertThat(checker.reasons().size(), is(not(0)));
            assertThat(result, areNotImmutable());
        }

        @FalsePositive("Field [precision] can be reassigned within method [precision]" + "Field [stringCache] can be reassigned within method [toString]"
                + "Field [intVal] can be reassigned within method [inflate]")
        @Test
        public void bigDecimalDoesNotFailCheck() throws Exception {
            assertThat(doCheck(BigDecimal.class), areImmutable());
        }

        @FalsePositive("Field [hash] can be reassigned within method [hashCode]")
        @Test
        public void stringDoesNotFailCheck() throws Exception {
            assertThat(doCheck(String.class), areImmutable());
        }

        @Test
        public void fieldReassignmentInPublicStaticMethodMakesClassMutable() throws Exception {
            AnalysisResult result = doCheck(MutableByAssigningFieldOnInstanceWithinStaticMethod.class);
            assertThat(result, areNotImmutable());
        }

        @FalsePositive("Field can be reassigned.")
        @Test
        public void reassignmentOfStackConfinedObjectDoesNotFailCheck() throws Exception {
            assertThat(doCheck(ImmutableWithMutatingStaticFactoryMethod.class), areImmutable());
        }

        @Test
        public void reassigningFieldWithNewedUpObjectShouldBeMutable() {
            assertThat(doCheck(MutableByAssigningFieldToNewedUpObject.class), areNotImmutable());
        }

        @Test
        public void codeLocationOfReasonIsAFieldLocation() throws Exception {
            CodeLocation<?> location = doCheck(ReassignsSingleField.class).reasons.iterator().next().codeLocation();
            
            assertThat(location, Matchers.instanceOf(FieldLocation.class));
            assertThat(((FieldLocation)location).fieldName(), is("reassigned"));
        }

        @Theory
        public void settingFieldsOfAnyTypeShouldBeMutable(Class<?> mutableSettingField) throws Exception {
            AnalysisResult result = runChecker(checker, mutableSettingField);
            assertThat(result, areNotImmutable());
        }

    }

}
