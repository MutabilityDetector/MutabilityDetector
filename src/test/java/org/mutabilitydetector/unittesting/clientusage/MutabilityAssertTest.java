package org.mutabilitydetector.unittesting.clientusage;

/*
 * #%L
 * MutabilityDetector
 * %%
 * Copyright (C) 2008 - 2014 Graham Allan
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import com.google.common.collect.Lists;
import org.junit.Rule;
import org.junit.Test;
import org.mutabilitydetector.benchmarks.ImmutableExample;
import org.mutabilitydetector.benchmarks.ImmutableProvidedOtherClassIsImmutable;
import org.mutabilitydetector.benchmarks.ImmutableProvidedOtherClassIsImmutable.ThisHasToBeImmutable;
import org.mutabilitydetector.benchmarks.MutableByHavingPublicNonFinalField;
import org.mutabilitydetector.benchmarks.mutabletofield.DependsOnManyTypesBeingImmutable;
import org.mutabilitydetector.benchmarks.mutabletofield.HasDateField;
import org.mutabilitydetector.benchmarks.mutabletofield.MutatesAsInternalCaching;
import org.mutabilitydetector.benchmarks.mutabletofield.generic.HasFieldOfGenericType;
import org.mutabilitydetector.benchmarks.mutabletofield.generic.HasFieldUsingGenericTypeOfClass;
import org.mutabilitydetector.benchmarks.mutabletofield.jdktypefields.HasAStringField;
import org.mutabilitydetector.benchmarks.mutabletofield.jdktypefields.HasCollectionField;
import org.mutabilitydetector.benchmarks.sealed.IsSubclassableAndDependsOnParameterBeingImmutable;
import org.mutabilitydetector.benchmarks.sealed.MutableByNotBeingFinalClass;
import org.mutabilitydetector.benchmarks.settermethod.MutableByHavingSetterMethod;
import org.mutabilitydetector.benchmarks.types.AbstractType;
import org.mutabilitydetector.benchmarks.types.InterfaceType;
import org.mutabilitydetector.benchmarks.visibility.AlmostEffectivelyImmutable;
import org.mutabilitydetector.junit.FalsePositive;
import org.mutabilitydetector.junit.IncorrectAnalysisRule;
import org.mutabilitydetector.unittesting.AllowedReason;
import org.mutabilitydetector.unittesting.MutabilityAssertionError;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mutabilitydetector.unittesting.AllowedReason.*;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutable;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.*;

public class MutabilityAssertTest {

    private final Class<?> immutableClass = ImmutableExample.class;
    private final Class<?> mutableClass = MutableByHavingPublicNonFinalField.class;

    private final String expectedError = String.format("%n" +
            "Expected: org.mutabilitydetector.benchmarks.MutableByHavingPublicNonFinalField to be IMMUTABLE%n" +
            "     but: org.mutabilitydetector.benchmarks.MutableByHavingPublicNonFinalField is actually NOT_IMMUTABLE%n" +
            "    Reasons:%n" +
            "        Can be subclassed, therefore parameters declared to be this type could be mutable subclasses at runtime. [Class: org.mutabilitydetector.benchmarks.MutableByHavingPublicNonFinalField]%n" +
            "        Field is visible outwith this class, and is not declared final. [Field: org.mutabilitydetector.benchmarks.MutableByHavingPublicNonFinalField.name]%n" +
            "        Field is not final, if shared across threads the Java Memory Model will not guarantee it is initialised before it is read. [Field: org.mutabilitydetector.benchmarks.MutableByHavingPublicNonFinalField.name]%n" +
            "    Allowed reasons:%n" +
            "        None.");

    @Test
    public void assertImmutableWithImmutableClassDoesNotThrowAssertionError() throws Exception {
        assertImmutable(immutableClass);
    }

    @Test(expected = MutabilityAssertionError.class)
    public void assertImmutableWithMutableClassThrowsAssertionError() throws Exception {
        assertImmutable(mutableClass);
    }

    @Test
    public void whenAssertImmutableFailsReasonsArePrintedWithAssertionFailure() throws Exception {
        try {
            assertImmutable(mutableClass);
            fail("Assertion should have failed.");
        } catch (final AssertionError ae) {
            assertEquals("", expectedError, ae.getMessage());
        }
    }

    @Test
    public void whenAssertInstancesOfFailsReasonsArePrintedWithAssertionFailure() throws Exception {
        try {
            assertInstancesOf(mutableClass, areImmutable());
            fail("Assertion should have failed.");
        } catch (final AssertionError ae) {
            assertThat(ae.getMessage(), equalTo(expectedError));
        }
    }

    @Test
    public void assertInstancesOfClassAreImmutableDoesNotFailForImmutableClass() throws Exception {
        assertInstancesOf(ImmutableExample.class, areImmutable());
    }

    @Test(expected = MutabilityAssertionError.class)
    public void assertThatIsImmutableFailsForMutableClass() throws Exception {
        assertInstancesOf(MutableByHavingPublicNonFinalField.class, areImmutable());
    }

    @Test
    public void failedMatchMessageFromAssertThatIsDescriptive() throws Exception {
        try {
            assertInstancesOf(mutableClass, areImmutable());
        } catch (AssertionError ae) {
            assertThat(ae.getMessage(), equalTo(expectedError));
        }
    }

    @Test public void canSpecifyMultipleAllowedReasons() {
         assertInstancesOf(IsSubclassableAndDependsOnParameterBeingImmutable.class,
                           areImmutable(),
                           allowingForSubclassing(),
                           provided(ThisHasToBeImmutable.class).isAlsoImmutable());

    }

    @SuppressWarnings("unchecked")
    @Test public void varArgsArgumentsCompilesAndExecutes() {
        assertInstancesOf(IsSubclassableAndDependsOnParameterBeingImmutable.class,
                          areImmutable(),
                          allowingForSubclassing(),
                          provided(ThisHasToBeImmutable.class).isAlsoImmutable(),
                          allowingForSubclassing(),
                          allowingForSubclassing(),
                          allowingForSubclassing());

   }

    @SuppressWarnings("unchecked")
    @Test public void iterableArgumentCompilesAndExecutes() {
        assertInstancesOf(IsSubclassableAndDependsOnParameterBeingImmutable.class,
                          areImmutable(),
                          Lists.newArrayList(allowingForSubclassing(),
                                             provided(ThisHasToBeImmutable.class).isAlsoImmutable(),
                                             allowingForSubclassing(),
                                             allowingForSubclassing(),
                                             allowingForSubclassing()));

    }

    @Test
    public void canSpecifyIsImmutableAsLongAsOtherClassIsImmutable() throws Exception {
        assertInstancesOf(ImmutableProvidedOtherClassIsImmutable.class,
                areImmutable(),
                provided(ThisHasToBeImmutable.class).isAlsoImmutable());

    }

    @Test
    public void canSpecifyIsImmutableAsLongAsGenericTypeIsImmutable() throws Exception {
        assertInstancesOf(HasFieldOfGenericType.class,
                areImmutable(),
                provided("T").isAlsoImmutable(),
                provided("N").isAlsoImmutable());
    }

    @Test
    public void canSpecifyIsImmutableAsLongAsGenericTypeUsedByFieldIsImmutable() throws Exception {
        assertInstancesOf(HasFieldUsingGenericTypeOfClass.class, areNotImmutable());
        assertInstancesOf(HasFieldUsingGenericTypeOfClass.class, areImmutable(), provided("MY_TYPE").isAlsoImmutable());
    }

    @Test(expected = MutabilityAssertionError.class)
    public void failsWhenAllowingReasonWhichIsNotTheCauseOfMutability() {
        assertInstancesOf(MutableByHavingSetterMethod.class,
                areImmutable(),
                provided(ThisHasToBeImmutable.class).isAlsoImmutable());
    }

    @Test
    public void providesUsefulFailureMessageWhenAssertionFails() {
        try {
            assertInstancesOf(MutableByHavingSetterMethod.class,
                    areImmutable(),
                    provided(ThisHasToBeImmutable.class).isAlsoImmutable());
        } catch (AssertionError e) {
            assertThat(e.getMessage(), containsString("can be reassigned"));
        }
    }

    @Test
    public void canAllowSubclassingForNonFinalClasses() throws Exception {
        assertInstancesOf(MutableByNotBeingFinalClass.class,
                areImmutable(),
                allowingForSubclassing());
    }


    @Test(expected = MutabilityAssertionError.class)
    public void allowSubclassingFailsWhenReasonIsDifferent() throws Exception {
        assertInstancesOf(MutableByHavingSetterMethod.class,
                areImmutable(),
                allowingForSubclassing());
    }

    @Test
    public void canMatchEffectivelyImmutableAllowingAnotherReason() throws Exception {
        assertInstancesOf(AlmostEffectivelyImmutable.class,
                          areEffectivelyImmutable(),
                          allowingNonFinalFields(),
                          allowingForSubclassing());
    }

    @Test
    public void canMatchImmutableAllowingAnotherReason() throws Exception {
        assertInstancesOf(AlmostEffectivelyImmutable.class,
                          areImmutable(),
                          allowingNonFinalFields(),
                          allowingForSubclassing());
    }

    @Rule public final IncorrectAnalysisRule incorrectAnalysisRule = new IncorrectAnalysisRule();

    /**
     * @see #canMatchEffectivelyImmutableAllowingAnotherReason() for a workaround
     */
    @FalsePositive("Issue 21: can't think of an elegant solution to this,")
    @Test
    public void canMatchEffectivelyImmutableAllowingAnotherReasonWithoutExplicitlyAllowingNonFinalFields() throws Exception {
        assertInstancesOf(AlmostEffectivelyImmutable.class,
                          areEffectivelyImmutable(),
                          allowingForSubclassing());
    }

    @Test
    public void havingStringFieldsDoesNotCauseFalsePositivesInTheDefaultConfiguration() throws Exception {
        assertImmutable(HasAStringField.class);
    }

    @Test
    public void allowSpecifyingThatMultipleTypesMustAlsoBeImmutable() throws Exception {
        assertInstancesOf(DependsOnManyTypesBeingImmutable.class,
                          areImmutable(),
                          provided(AbstractType.class, InterfaceType.class).isAlsoImmutable());
    }

    @FalsePositive("Does not work when specifying two different ProvidedOtherClass reasons.")
    @Test
    public void allowSpecifyingThatMultipleTypesMustAlsoBeImmutable_does_not_work_with_separate_provided_calls() throws Exception {
        assertInstancesOf(DependsOnManyTypesBeingImmutable.class,
                          areImmutable(),
                          provided(AbstractType.class).isAlsoImmutable(),
                          provided(InterfaceType.class).isAlsoImmutable());
    }

    @Test
    public void canAllowCollectionFieldsDeemedToHaveBeenSafelyCopiedAndWrappedInUnmodifiable() throws Exception {
        assertInstancesOf(HasCollectionField.class,
                          areImmutable(),
                          AllowedReason.assumingFields("myStrings").areSafelyCopiedUnmodifiableCollectionsWithImmutableElements());
    }

    @Test
    public void canAllowAMutableFieldWhicIsNotMutatedAndDoesNotEscape() throws Exception {
        assertInstancesOf(HasDateField.class,
                          areImmutable(),
                          AllowedReason.assumingFields("myDate").areNotModifiedAndDoNotEscape());
    }

    @Test
    public void canAllowInternalCachingWhichCausesUnobservableMutation() throws Exception {
        assertInstancesOf(MutatesAsInternalCaching.class,
                          areImmutable(),
                          AllowedReason.assumingFields("lengthWhenConcatenated").areModifiedAsPartOfAnUnobservableCachingStrategy());
    }

}
