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
import static org.mutabilitydetector.checkers.info.Dotted.dotted;
import static org.mutabilitydetector.checkers.util.TypeStructureInformationChecker.newChecker;

import org.junit.Test;
import org.mutabilitydetector.CheckerRunner;
import org.mutabilitydetector.benchmarks.types.AbstractType;
import org.mutabilitydetector.benchmarks.types.ConcreteType;
import org.mutabilitydetector.checkers.info.Dotted;


public class TypeStructureInformationCheckerTest {

	private CheckerRunner checkerRunner = CheckerRunner.createWithCurrentClasspath();;

	@Test public void isAbstractIsTrueForAbstractType() throws Exception {
		TypeStructureInformationChecker checker = newChecker();
		checkerRunner.run(checker, AbstractType.class);
		
		Dotted dottedClassName = dotted(AbstractType.class.getName());
		boolean isAbstract = checker.isAbstract(dottedClassName);
		assertTrue("Class is abstract.", isAbstract);
		
	}
	
	@Test public void isAbstractIsFalseForConcreteType() throws Exception {
		TypeStructureInformationChecker checker = newChecker();
		checkerRunner.run(checker, ConcreteType.class);
		
		boolean isAbstract = checker.isAbstract(dotted(ConcreteType.class.getName()));
		assertFalse("Class is concrete.", isAbstract);
	}
	
}
