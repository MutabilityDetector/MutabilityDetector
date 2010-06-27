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

import org.junit.Before;
import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.CheckerRunner;
import org.mutabilitydetector.IAnalysisSession;
import org.mutabilitydetector.ImmutableAssert;
import org.mutabilitydetector.benchmarks.types.EnumType;
import org.mutabilitydetector.checkers.InheritedMutabilityChecker;


public class InheritedMutabilityCheckerTest {

	InheritedMutabilityChecker checker;
	CheckerRunner runner;
	IAnalysisSession session;
	
	@Before
	public void setUp() {
		runner = CheckerRunner.createWithCurrentClasspath();
		session =  new AnalysisSession();
		checker = new InheritedMutabilityChecker(session);
	}
	
	//@Test TODO work in progress.
	public void testEnumTypeIsDefinitelyImmutable() throws Exception {
		runner.run(checker, EnumType.class);
		
		ImmutableAssert.assertImmutable(checker.result());
	}
}
