/* 
 * Mutability Detector
 *
 * Copyright 2009 Graham Allan
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.mutabilitydetector.benchmarks;

import static org.junit.Assert.assertEquals;
import static org.mutabilitydetector.AnalysisSession.createWithCurrentClassPath;
import static org.mutabilitydetector.CheckerRunner.createWithCurrentClasspath;
import static org.mutabilitydetector.ImmutableAssert.assertDefinitelyNotImmutable;
import static org.mutabilitydetector.ImmutableAssert.assertImmutable;

import org.junit.Before;
import org.junit.Test;
import org.mutabilitydetector.checkers.AbstractTypeToFieldChecker;
import org.mutabilitydetector.checkers.IMutabilityChecker;
import org.mutabilitydetector.checkers.info.SessionCheckerRunner;
import org.mutabilitydetector.checkers.info.TypeStructureInformation;



public class AbstractTypeToFieldCheckerTest {

	IMutabilityChecker checker;

	@Before public void setUp() {
		SessionCheckerRunner runner = new SessionCheckerRunner(createWithCurrentClassPath(), 
															   createWithCurrentClasspath());
		TypeStructureInformation typeInfo = new TypeStructureInformation(runner);
		checker = new AbstractTypeToFieldChecker(typeInfo);
	}

	@Test public void testImmutableExamplePassesCheck() throws Exception {
		createWithCurrentClasspath().run(checker, ImmutableExample.class);

		assertImmutable(checker.result());		
		assertEquals(checker.reasons().size(), 0);
	}
	
	@Test public void testMutableByAssigningInterfaceTypeToFieldFailsCheck() throws Exception {
		createWithCurrentClasspath().run(checker, MutableByAssigningInterfaceToField.class);
		
		assertDefinitelyNotImmutable(checker.result());
	}
	
	@Test public void testMutableByAssigningAbstractClassToFieldFailsCheck() throws Exception {
		createWithCurrentClasspath().run(checker, MutableByAssigningAbstractTypeToField.class);
		assertDefinitelyNotImmutable(checker.result());
	}

}
