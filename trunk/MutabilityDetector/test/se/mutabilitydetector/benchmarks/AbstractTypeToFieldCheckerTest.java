package se.mutabilitydetector.benchmarks;

import static org.junit.Assert.assertEquals;
import static se.mutabilitydetector.ImmutableAssert.assertDefinitelyNotImmutable;
import static se.mutabilitydetector.ImmutableAssert.assertImmutable;

import org.junit.Before;
import org.junit.Test;

import se.mutabilitydetector.CheckerRunner;
import se.mutabilitydetector.checkers.AbstractTypeToFieldChecker;
import se.mutabilitydetector.checkers.IMutabilityChecker;

public class AbstractTypeToFieldCheckerTest {

	IMutabilityChecker checker;

	@Before
	public void setUp() {
		checker = new AbstractTypeToFieldChecker();
	}

	@Test
	public void testImmutableExamplePassesCheck() throws Exception {
		new CheckerRunner(null).run(checker, ImmutableExample.class);

		assertImmutable(checker.result());		
		assertEquals(checker.reasons().size(), 0);
	}
	
	@Test
	public void testMutableByAssigningInterfaceTypeToFieldFailsCheck() throws Exception {
		new CheckerRunner(null).run(checker, MutableByAssigningInterfaceToField.class);
		
		assertDefinitelyNotImmutable(checker.result());
	}
	
	@Test
	public void testMutableByAssigningAbstractClassToFieldFailsCheck() throws Exception {
		new CheckerRunner(null).run(checker, MutableByAssigningAbstractTypeToField.class);
		assertDefinitelyNotImmutable(checker.result());
	}

}
