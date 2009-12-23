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
