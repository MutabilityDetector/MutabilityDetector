package se.mutabilitydetector.benchmarks.inheritance;
import static se.mutabilitydetector.ImmutableAssert.assertDefinitelyNotImmutable;
import static se.mutabilitydetector.ImmutableAssert.assertImmutable;
import static se.mutabilitydetector.ImmutableAssert.assertIsImmutableResult;
import static se.mutabilitydetector.TestUtil.getResultOfAnalysis;

import org.junit.Test;

import se.mutabilitydetector.ImmutableAssert;
import se.mutabilitydetector.IAnalysisSession.IsImmutable;

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
