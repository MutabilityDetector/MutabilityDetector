/*
 *    Copyright (c) 2008-2011 Graham Allan
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

package org.mutabilitydetector.unittesting;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.mutabilitydetector.unittesting.AllowedReason.allowingForSubclassing;
import static org.mutabilitydetector.unittesting.AllowedReason.allowingNonFinalFields;
import static org.mutabilitydetector.unittesting.AllowedReason.provided;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutable;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areEffectivelyImmutable;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;

import org.junit.Rule;
import org.junit.Test;
import org.mutabilitydetector.benchmarks.ImmutableExample;
import org.mutabilitydetector.benchmarks.ImmutableProvidedOtherClassIsImmutable;
import org.mutabilitydetector.benchmarks.ImmutableProvidedOtherClassIsImmutable.ThisHasToBeImmutable;
import org.mutabilitydetector.benchmarks.MutableByHavingPublicNonFinalField;
import org.mutabilitydetector.benchmarks.mutabletofield.DependsOnManyTypesBeingImmutable;
import org.mutabilitydetector.benchmarks.mutabletofield.jdktypefields.HasAStringField;
import org.mutabilitydetector.benchmarks.sealed.IsSubclassableAndDependsOnParameterBeingImmutable;
import org.mutabilitydetector.benchmarks.sealed.MutableByNotBeingFinalClass;
import org.mutabilitydetector.benchmarks.settermethod.MutableByHavingSetterMethod;
import org.mutabilitydetector.benchmarks.types.AbstractType;
import org.mutabilitydetector.benchmarks.types.InterfaceType;
import org.mutabilitydetector.benchmarks.visibility.AlmostEffectivelyImmutable;
import org.mutabilitydetector.junit.FalsePositive;
import org.mutabilitydetector.junit.IncorrectAnalysisRule;

import com.google.common.collect.Lists;

public class MutabilityAssertTest {

    private final Class<?> immutableClass = ImmutableExample.class;
    private final Class<?> mutableClass = MutableByHavingPublicNonFinalField.class;
    
    private final String expectedError = String.format("%n" +
            "Expected: org.mutabilitydetector.benchmarks.MutableByHavingPublicNonFinalField to be IMMUTABLE%n" + 
            "     but: org.mutabilitydetector.benchmarks.MutableByHavingPublicNonFinalField is actually NOT_IMMUTABLE%n" + 
            "    Reasons:%n" + 
            "        Can be subclassed, therefore parameters declared to be this type could be mutable subclasses at runtime. [Class: org.mutabilitydetector.benchmarks.MutableByHavingPublicNonFinalField]%n" + 
            "        Field is not final, if shared across threads the Java Memory Model will not guarantee it is initialised before it is read. [Field: name, Class: org.mutabilitydetector.benchmarks.MutableByHavingPublicNonFinalField]%n" + 
            "        Field is visible outwith this class, and is not declared final. [Field: name, Class: org.mutabilitydetector.benchmarks.MutableByHavingPublicNonFinalField]%n" + 
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
            assertThat(ae.getMessage(), equalTo(expectedError));
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

    @Rule public IncorrectAnalysisRule incorrectAnalysisRule = new IncorrectAnalysisRule();
    
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

}
