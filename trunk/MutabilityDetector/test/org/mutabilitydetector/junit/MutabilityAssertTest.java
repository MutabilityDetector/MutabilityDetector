/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector.junit;

import static java.lang.String.format;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.matchers.JUnitMatchers;
import org.mutabilitydetector.IAnalysisSession.IsImmutable;
import org.mutabilitydetector.benchmarks.ImmutableExample;
import org.mutabilitydetector.benchmarks.MutableByHavingPublicNonFinalField;

public class MutabilityAssertTest {

	private Class<?> immutableClass = ImmutableExample.class;
	private Class<?> mutableClass = MutableByHavingPublicNonFinalField.class;

	@Test
	public void testAssertImmutableWithImmutableClass() throws Exception {
		// No AssertionException means test passes
		MutabilityAssert.assertImmutable(immutableClass);
	}

	@Test(expected = AssertionError.class)
	public void testAssertImmutableWithMutableClass() throws Exception {
		MutabilityAssert.assertImmutable(mutableClass);
	}

	@Test
	public void testReasonsArePrintedWithAssertionFailure() throws Exception {
		try {
			MutabilityAssert.assertImmutable(mutableClass);
			fail("Assertion should have failed.");
		} catch (final AssertionError ae) {
			String expectedPrefix = format(
					"Expected %s to be DEFINITELY immutable. Was: DEFINITELY_NOT immutable.", mutableClass
							.getSimpleName());
			assertThat(ae.getMessage(), JUnitMatchers.containsString(expectedPrefix));
		}
	}
	
	@Test
	public void testAssertImmutableStatusIsPassesWhenBothAreEqual() throws Exception {
		MutabilityAssert.assertImmutableStatusIs(IsImmutable.DEFINITELY, immutableClass);
	}

	@Test
	public void testAssertImmutableStatusIsFailsWhenUnequal() throws Exception {
		try {
			MutabilityAssert.assertImmutableStatusIs(IsImmutable.DEFINITELY_NOT, immutableClass);
		} catch (final AssertionError ae) {
			String expectedMessage = format("expected:<%s> but was:<%s>", IsImmutable.DEFINITELY_NOT,
					IsImmutable.DEFINITELY);
			assertThat(ae.getMessage(), JUnitMatchers.containsString(expectedMessage));
		}
	}
}
