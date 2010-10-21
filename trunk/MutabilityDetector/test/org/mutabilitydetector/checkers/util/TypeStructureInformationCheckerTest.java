/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector.checkers.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mutabilitydetector.checkers.util.TypeStructureInformationChecker.newChecker;
import static org.mutabilitydetector.locations.Dotted.fromClass;

import org.junit.Test;
import org.mutabilitydetector.CheckerRunner;
import org.mutabilitydetector.benchmarks.types.AbstractType;
import org.mutabilitydetector.benchmarks.types.ConcreteType;
import org.mutabilitydetector.locations.Dotted;


public class TypeStructureInformationCheckerTest {

	private CheckerRunner checkerRunner = CheckerRunner.createWithCurrentClasspath();;

	@Test public void isAbstractIsTrueForAbstractType() throws Exception {
		Dotted className = fromClass(AbstractType.class);
		TypeStructureInformationChecker checker = newChecker(className);
		checkerRunner.run(checker, AbstractType.class);
		
		assertTrue("Class is abstract.", checker.isAbstract());
		
	}
	
	@Test public void isAbstractIsFalseForConcreteType() throws Exception {
		Dotted className = fromClass(ConcreteType.class);
		TypeStructureInformationChecker checker = newChecker(className);
		checkerRunner.run(checker, ConcreteType.class);
		
		assertFalse("Class is concrete.", checker.isAbstract());
	}
	
}
