package se.mutabilitydetector.benchmarks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import se.mutabilitydetector.IAnalysisSession.IsImmutable;

public class ImmutableAssert {

	public static void assertImmutable(IsImmutable result) {
		assertEquals("Expected Immutable result.", IsImmutable.DEFINITELY, result);
	}
	
	public static void assertDefinitelyNotImmutable(IsImmutable result) {
		assertEquals("Expected Not Immutable result.", IsImmutable.DEFINITELY_NOT, result);
	}

	public static void assertNotImmutable(IsImmutable result) {
		String error = "Expected any result but Immutable. \nActual: " + result.name();
		assertFalse(error, IsImmutable.DEFINITELY.equals(result));
		
	}
	
}
