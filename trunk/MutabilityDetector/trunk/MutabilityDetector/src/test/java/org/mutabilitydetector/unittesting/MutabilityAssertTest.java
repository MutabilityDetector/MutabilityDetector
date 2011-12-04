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

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.mutabilitydetector.IsImmutable.IMMUTABLE;
import static org.mutabilitydetector.IsImmutable.NOT_IMMUTABLE;
import static org.mutabilitydetector.unittesting.AllowedReason.allowingForSubclassing;
import static org.mutabilitydetector.unittesting.AllowedReason.allowingNonFinalFields;
import static org.mutabilitydetector.unittesting.AllowedReason.provided;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutable;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areEffectivelyImmutable;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;

import org.junit.Test;
import org.mutabilitydetector.benchmarks.ImmutableExample;
import org.mutabilitydetector.benchmarks.ImmutableProvidedOtherClassIsImmutable;
import org.mutabilitydetector.benchmarks.ImmutableProvidedOtherClassIsImmutable.ThisHasToBeImmutable;
import org.mutabilitydetector.benchmarks.MutableByHavingPublicNonFinalField;
import org.mutabilitydetector.benchmarks.MutableByNotBeingFinalClass;
import org.mutabilitydetector.benchmarks.finalfield.AlmostEffectivelyImmutable;
import org.mutabilitydetector.benchmarks.sealed.IsSubclassableAndDependsOnParameterBeingImmutable;
import org.mutabilitydetector.benchmarks.settermethod.MutableByHavingSetterMethod;

public class MutabilityAssertTest {

    private final Class<?> immutableClass = ImmutableExample.class;
    private final Class<?> mutableClass = MutableByHavingPublicNonFinalField.class;

    @Test
    public void assertImmutableWithImmutableClassDoesNotThrowAssertionError() throws Exception {
        assertImmutable(immutableClass);
    }

    @Test(expected = MutabilityAssertionError.class)
    public void assertImmutableWithMutableClassThrowsAssertionError() throws Exception {
        assertImmutable(mutableClass);
    }

    @Test
    public void reasonsArePrintedWithAssertionFailure() throws Exception {
        try {
            assertImmutable(mutableClass);
            fail("Assertion should have failed.");
        } catch (final AssertionError ae) {
            assertThat(ae.getMessage(), containsString(mutableClass.getSimpleName()));
            assertThat(ae.getMessage(), containsString(IMMUTABLE.name()));
            assertThat(ae.getMessage(), containsString(NOT_IMMUTABLE.name()));
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
            assertInstancesOf(MutableByHavingPublicNonFinalField.class, areImmutable());
        } catch (AssertionError ae) {
            assertThat(ae.getMessage(), containsString(IMMUTABLE.name()));
            assertThat(ae.getMessage(), containsString(NOT_IMMUTABLE.name()));
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
    
    
    
}
