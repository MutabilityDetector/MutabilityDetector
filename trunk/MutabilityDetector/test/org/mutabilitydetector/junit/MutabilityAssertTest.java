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

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.matchers.JUnitMatchers;
import org.mutabilitydetector.benchmarks.ImmutableExample;
import org.mutabilitydetector.benchmarks.MutableByHavingPublicNonFinalField;

public class MutabilityAssertTest {

	private Class<?> immutableClass = ImmutableExample.class;
	private Class<?> mutableClass = MutableByHavingPublicNonFinalField.class;

	@Test
	// No assertion exception means test passes
	public void testAssertImmutableWithImmutableClass() throws Exception {
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
			String expectedPrefix = String.format(
					"Expected %s to be DEFINITELY immutable. Was: DEFINITELY_NOT immutable.", mutableClass
							.getSimpleName());
			assertThat(ae.getMessage(), JUnitMatchers.containsString(expectedPrefix));
		}
	}

}
