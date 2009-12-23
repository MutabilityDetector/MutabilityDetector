/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */
package org.mutabilitydetector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.mutabilitydetector.IAnalysisSession.IsImmutable;

public class ImmutableAssert {

	public static void assertImmutable(IsImmutable result) {
		String failString = "Expected Immutable result.\n";
		assertEquals(failString, IsImmutable.DEFINITELY, result);
	}
	
	public static void assertDefinitelyNotImmutable(IsImmutable result) {
		assertEquals("Expected Not Immutable result.", IsImmutable.DEFINITELY_NOT, result);
	}

	public static void assertNotImmutable(IsImmutable result) {
		String error = "Expected any result but Immutable. \nActual: " + result.name();
		assertFalse(error, IsImmutable.DEFINITELY.equals(result));
		
	}
	
	public static void assertIsImmutableResult(IsImmutable expected, IsImmutable actual) {
		assertEquals(expected, actual);
	}
	
}
