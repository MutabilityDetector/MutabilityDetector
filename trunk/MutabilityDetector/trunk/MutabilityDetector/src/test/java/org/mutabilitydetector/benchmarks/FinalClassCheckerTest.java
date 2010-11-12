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
import static org.mutabilitydetector.ImmutableAssert.assertMaybeImmutable;
import static org.mutabilitydetector.TestUtil.runChecker;

import org.junit.Before;
import org.junit.Test;
import org.mutabilitydetector.benchmarks.types.EnumType;
import org.mutabilitydetector.checkers.FinalClassChecker;

public class FinalClassCheckerTest {

	private FinalClassChecker checker;

	@Before public void createChecker() {
		checker = new FinalClassChecker();
	}

	@Test public void aClassWhichIsNotFinalIsMaybeImmutable() throws Exception {
		assertMaybeImmutable(runChecker(checker, MutableByNotBeingFinalClass.class));
		assertTrue("There should be a reason given when the class is not immutable.", checker.reasons().size() > 0);
	}

	
	@Test public void immutableExampleIsReportedAsImmutable() throws Exception {
		assertImmutable(runChecker(checker, ImmutableExample.class));
	}
	
	@Test public void enumTypeIsImmutable() throws Exception {
		assertImmutable(runChecker(checker, EnumType.class));
	}
	
}
