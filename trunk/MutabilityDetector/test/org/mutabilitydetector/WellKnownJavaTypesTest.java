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

import static org.mutabilitydetector.ImmutableAssert.assertImmutable;
import static org.mutabilitydetector.ImmutableAssert.assertMaybeImmutable;
import static org.mutabilitydetector.ImmutableAssert.assertNotImmutable;

import java.lang.reflect.Array;
import java.util.Date;

import org.junit.Test;

public class WellKnownJavaTypesTest {
	@Test public void testWellKnownJavaTypes() throws Exception {
		assertImmutable(Integer.class);
		assertImmutable(int.class);
		assertImmutable(Array.class);
		// the hash code field is lazily computed, and renders String
		// mutable
		// assertImmutable(String.class);
		assertMaybeImmutable(Object.class);
		assertNotImmutable(Date.class);

	}
}