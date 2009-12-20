package se.mutabilitydetector.benchmarks;

import static org.junit.Assert.assertEquals;
import static se.mutabilitydetector.benchmarks.ImmutableAssert.assertDefinitelyNotImmutable;
import static se.mutabilitydetector.benchmarks.ImmutableAssert.assertImmutable;

import org.junit.Before;
import org.junit.Test;

import se.mutabilitydetector.CheckerRunner;
import se.mutabilitydetector.checkers.IMutabilityChecker;
import se.mutabilitydetector.checkers.PublishedNonFinalFieldChecker;


public class PublishedNonFinalFieldCheckerTest {

	private IMutabilityChecker checker;
	
	@Before
	public void setUp() {
		checker = new PublishedNonFinalFieldChecker();
	}
	
	@Test
	public void testImmutableExamplePassesCheck() throws Exception {
		new CheckerRunner(null).run(checker, ImmutableExample.class);
		assertImmutable(checker.result());
		assertEquals(0, checker.reasons().size());
	}
	
	
	@Test
	public void testClassWithPublicNonFinalFieldFailsCheck() throws Exception {
		new CheckerRunner(null).run(checker, MutableByHavingPublicNonFinalField.class);
		assertDefinitelyNotImmutable(checker.result());
	}
	
	@Test
	public void testClassWithProtectedNonFinalFieldFailsCheck() throws Exception {
		new CheckerRunner(null).run(checker, MutableByHavingProtectedNonFinalField.class);
		assertDefinitelyNotImmutable(checker.result());
	}
	
	@Test
	public void testClassWithDefaultVisibleNonFinalFieldFailsCheck() throws Exception {
		new CheckerRunner(null).run(checker, MutableByHavingDefaultVisibleNonFinalField.class);
		assertDefinitelyNotImmutable(checker.result());
	}
	
	@Test
	public void testClassWithPublicFinalFieldPassesCheck() throws Exception {
		new CheckerRunner(null).run(checker, ImmutableWithPublicFinalField.class);
		assertImmutable(checker.result());
		assertEquals(0, checker.reasons().size());
	}
}
