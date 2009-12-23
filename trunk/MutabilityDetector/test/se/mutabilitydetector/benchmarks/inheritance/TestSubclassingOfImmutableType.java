package se.mutabilitydetector.benchmarks.inheritance;


import static se.mutabilitydetector.ImmutableAssert.assertDefinitelyNotImmutable;
import static se.mutabilitydetector.TestUtil.getResultOfAnalysis;

import org.junit.Test;

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
