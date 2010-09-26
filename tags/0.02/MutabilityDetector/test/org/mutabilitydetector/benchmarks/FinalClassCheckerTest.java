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

import static org.junit.Assert.assertTrue;
import static org.mutabilitydetector.ImmutableAssert.assertImmutable;
import static org.mutabilitydetector.ImmutableAssert.assertNotImmutable;

import org.junit.Before;
import org.junit.Test;
import org.mutabilitydetector.CheckerRunner;
import org.mutabilitydetector.benchmarks.types.EnumType;
import org.mutabilitydetector.checkers.FinalClassChecker;




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
	
	@Test
	public void testEnumTypeIsImmutable() throws Exception {
		runChecker(EnumType.class);
		assertImmutable(finalFieldsChecker.result());
	}
	
}
