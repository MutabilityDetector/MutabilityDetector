/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */
package org.mutabilitydetector.benchmarks.inheritance;


import static org.mutabilitydetector.ImmutableAssert.assertDefinitelyNotImmutable;
import static org.mutabilitydetector.TestUtil.getResultOfAnalysis;

import org.junit.Test;
import org.mutabilitydetector.benchmarks.inheritance.ImmutableSubtypeOfMutableSupertype;
import org.mutabilitydetector.benchmarks.inheritance.MutableSubtypeOfMutableSupertype;
import org.mutabilitydetector.benchmarks.inheritance.MutableSupertype;


public class TestSubclassingOfImmutableType {

	@Test
	public void testSupertypeIsDefinitelyNotImmutable() throws Exception {
		assertDefinitelyNotImmutable(getResultOfAnalysis(MutableSupertype.class));
	}
	
	@Test
	public void testImmutableSubtypeIsReportedAsImmutable() throws Exception {
		assertDefinitelyNotImmutable(getResultOfAnalysis(ImmutableSubtypeOfMutableSupertype.class));
	}

	
	@Test
	public void testMutableSubtype() throws Exception {
		assertDefinitelyNotImmutable(getResultOfAnalysis(MutableSubtypeOfMutableSupertype.class));
	}
}
