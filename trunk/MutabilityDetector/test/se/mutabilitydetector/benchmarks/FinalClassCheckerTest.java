package se.mutabilitydetector.benchmarks;

import static org.junit.Assert.assertTrue;
import static se.mutabilitydetector.ImmutableAssert.assertImmutable;
import static se.mutabilitydetector.ImmutableAssert.assertNotImmutable;

import org.junit.Before;
import org.junit.Test;

import se.mutabilitydetector.CheckerRunner;
import se.mutabilitydetector.checkers.FinalClassChecker;


public class FinalClassCheckerTest {


	private FinalClassChecker finalFieldsChecker;

	@Before
	public void createChecker() {
		finalFieldsChecker = new FinalClassChecker();
	}

	@Test
	public void testAnalyseAClassWhichIsNotFinalMakesIsImmutableReturnFalse() throws Exception {
		runChecker(MutableByNotBeingFinalClass.class);
		
		assertNotImmutable(finalFieldsChecker.result());
		assertTrue("There should be a reason given when the class is not immutable.", finalFieldsChecker.reasons().size() > 0);
	}

	private void runChecker(Class<?> classToCheck) {
		new CheckerRunner(null).run(finalFieldsChecker, classToCheck);
	}
	
	
	@Test
	public void testImmutableExampleIsReportedAsImmutable() throws Exception {
		runChecker(ImmutableExample.class);
		assertImmutable(finalFieldsChecker.result());
		
	}
	
	
}
