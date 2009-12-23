/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */
package org.mutabilitydetector.benchmarks;

import static org.junit.Assert.assertEquals;
import static org.mutabilitydetector.ImmutableAssert.assertDefinitelyNotImmutable;
import static org.mutabilitydetector.ImmutableAssert.assertImmutable;

import java.util.Collections;

import org.junit.Test;
import org.mutabilitydetector.CheckerRunner;
import org.mutabilitydetector.benchmarks.ImmutableExample;
import org.mutabilitydetector.benchmarks.MutableByHavingSetterMethod;
import org.mutabilitydetector.checkers.SetterMethodChecker;




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
