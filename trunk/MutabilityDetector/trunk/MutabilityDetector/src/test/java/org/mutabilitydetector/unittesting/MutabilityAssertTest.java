/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector.unittesting;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.mutabilitydetector.IAnalysisSession.IsImmutable.DEFINITELY;
import static org.mutabilitydetector.IAnalysisSession.IsImmutable.DEFINITELY_NOT;
import static org.mutabilitydetector.unittesting.AllowedReason.provided;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutable;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutableStatusIs;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
import static org.mutabilitydetector.unittesting.matchers.MutabilityMatchers.areImmutable;

import org.junit.Test;
import org.mutabilitydetector.IAnalysisSession.IsImmutable;
import org.mutabilitydetector.benchmarks.ImmutableExample;
import org.mutabilitydetector.benchmarks.ImmutableProvidedOtherClassIsImmutable;
import org.mutabilitydetector.benchmarks.MutableByHavingPublicNonFinalField;
import org.mutabilitydetector.benchmarks.ImmutableProvidedOtherClassIsImmutable.ThisHasToBeImmutable;
import org.mutabilitydetector.benchmarks.settermethod.MutableByHavingSetterMethod;

public class MutabilityAssertTest {

	private Class<?> immutableClass = ImmutableExample.class;
	private Class<?> mutableClass = MutableByHavingPublicNonFinalField.class;

	@Test public void assertImmutableWithImmutableClassDoesNotThrowAssertionError() throws Exception {
		assertImmutable(immutableClass);
	}

	@Test(expected = MutabilityAssertionError.class)
	public void assertImmutableWithMutableClassThrowsAssertionError() throws Exception {
		assertImmutable(mutableClass);
	}

	@Test public void reasonsArePrintedWithAssertionFailure() throws Exception {
		try {
			assertImmutable(mutableClass);
			fail("Assertion should have failed.");
		} catch (final AssertionError ae) {
			assertThat(ae.getMessage(), containsString(mutableClass.getSimpleName()));
			assertThat(ae.getMessage(), containsString(DEFINITELY.name()));
			assertThat(ae.getMessage(), containsString(DEFINITELY_NOT.name()));
		}
	}
	
	@Test public void assertImmutableStatusIsPassesWhenBothAreEqual() throws Exception {
		assertImmutableStatusIs(IsImmutable.DEFINITELY, immutableClass);
	}

	@Test public void assertImmutableStatusIsFailsWhenUnequal() throws Exception {
		try {
			assertImmutableStatusIs(DEFINITELY_NOT, immutableClass);
		} catch (final AssertionError ae) {
			assertThat(ae.getMessage(), containsString(DEFINITELY.name()));
			assertThat(ae.getMessage(), containsString(DEFINITELY_NOT.name()));
		}
	}
	
	
	@Test public void assertInstancesOfClassAreImmutableDoesNotFailForImmutableClass() throws Exception {
		assertInstancesOf(ImmutableExample.class, areImmutable());
	}
	
	@Test(expected=AssertionError.class)
	public void assertThatIsImmutableFailsForMutableClass() throws Exception {
		assertInstancesOf(MutableByHavingPublicNonFinalField.class, areImmutable());
	}
	
	@Test public void failedMatchMessageFromAssertThatIsDescriptive() throws Exception {
		try {
			assertInstancesOf(MutableByHavingPublicNonFinalField.class, areImmutable());
		} catch (AssertionError ae) {
			assertThat(ae.getMessage(), containsString(DEFINITELY.name()));
			assertThat(ae.getMessage(), containsString(DEFINITELY_NOT.name()));
		}
	}
	
	@Test public void canSpecifyIsImmutableAsLongAsOtherClassIsImmutable() throws Exception {
		assertInstancesOf(ImmutableProvidedOtherClassIsImmutable.class, areImmutable(), 
				provided(ThisHasToBeImmutable.class).isAlsoImmutable());
		
	}
	
	@Test(expected=AssertionError.class)
	public void failsWhenAllowingReasonWhichIsNotTheCauseOfMutability() {
		assertInstancesOf(MutableByHavingSetterMethod.class, areImmutable(), 
				provided(ThisHasToBeImmutable.class).isAlsoImmutable());
	}
	
	@Test
	public void providesUsefulFailureMessageWhenAssertionFails() {
		try {
		assertInstancesOf(MutableByHavingSetterMethod.class, areImmutable(), 
				provided(ThisHasToBeImmutable.class).isAlsoImmutable());
		} catch (AssertionError e) {
			assertThat(e.getMessage(), containsString("can be reassigned"));
		}
	}



}
