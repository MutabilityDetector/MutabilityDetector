package org.mutabilitydetector.benchmarks.inheritance;
import static org.mutabilitydetector.ImmutableAssert.assertDefinitelyNotImmutable;
import static org.mutabilitydetector.ImmutableAssert.assertImmutable;
import static org.mutabilitydetector.ImmutableAssert.assertIsImmutableResult;
import static org.mutabilitydetector.TestUtil.getResultOfAnalysis;

import org.junit.Test;
import org.mutabilitydetector.ImmutableAssert;
import org.mutabilitydetector.IAnalysisSession.IsImmutable;
import org.mutabilitydetector.benchmarks.inheritance.ImmutableSubtypeOfImmutableSupertype;
import org.mutabilitydetector.benchmarks.inheritance.ImmutableSubtypeWithNoSuperclass;
import org.mutabilitydetector.benchmarks.inheritance.ImmutableSupertype;
import org.mutabilitydetector.benchmarks.inheritance.MutableSubtypeOfImmutableSupertype;


public class TestSubclassingOfMutableType {


	@Test
	public void testSupertypeIsMaybeImmutable() throws Exception {
		assertIsImmutableResult(IsImmutable.MAYBE, getResultOfAnalysis(ImmutableSupertype.class));
	}
	
	@Test
	public void testImmutableSubtypeIsReportedAsImmutable() throws Exception {
		assertImmutable(getResultOfAnalysis(ImmutableSubtypeOfImmutableSupertype.class));
	}

	
	@Test
	public void testMutableSubtype() throws Exception {
		assertDefinitelyNotImmutable(getResultOfAnalysis(MutableSubtypeOfImmutableSupertype.class));
	}
	
	@Test
	public void testClassExtendingObjectIsNotRenderedMutable() throws Exception {
		ImmutableAssert.assertImmutable(getResultOfAnalysis(ImmutableSubtypeWithNoSuperclass.class));
	}
}
