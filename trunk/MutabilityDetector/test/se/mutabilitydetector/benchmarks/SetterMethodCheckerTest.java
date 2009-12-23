package se.mutabilitydetector.benchmarks;

import static org.junit.Assert.assertEquals;
import static se.mutabilitydetector.ImmutableAssert.assertDefinitelyNotImmutable;
import static se.mutabilitydetector.ImmutableAssert.assertImmutable;

import java.util.Collections;

import org.junit.Test;

import se.mutabilitydetector.CheckerRunner;
import se.mutabilitydetector.checkers.SetterMethodChecker;


public class SetterMethodCheckerTest {

	private SetterMethodChecker checker;
	
	@Test
	public void testImmutableExamplePassesCheck() throws Exception {
		checker = new SetterMethodChecker();
		new CheckerRunner(null).run(checker, ImmutableExample.class);
		
		assertImmutable(checker.result());
		assertEquals(checker.reasons().size(), 0);
	}
	
	@Test
	public void testMutableByHavingSetterMethodFailsCheck() throws Exception {
		checker = new SetterMethodChecker();
		new CheckerRunner(null).run(checker, MutableByHavingSetterMethod.class);
		
		assertDefinitelyNotImmutable(checker.result());
	}
	
	@Test
	public void testIntegerClassPassesCheck() throws Exception {
		checker = new SetterMethodChecker();
		new CheckerRunner(null).run(checker, Integer.class);
		
		assertEquals(Collections.EMPTY_LIST, checker.reasons());
		assertImmutable(checker.result());
	}
}
