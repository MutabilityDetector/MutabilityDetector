/* 
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
import static org.mutabilitydetector.ImmutableAssert.assertDefinitelyNotImmutable;
import static org.mutabilitydetector.ImmutableAssert.assertImmutable;

import org.junit.Before;
import org.junit.Test;
import org.mutabilitydetector.CheckerRunner;
import org.mutabilitydetector.benchmarks.ImmutableExample;
import org.mutabilitydetector.benchmarks.ImmutableWithPublicFinalField;
import org.mutabilitydetector.benchmarks.MutableByHavingDefaultVisibleNonFinalField;
import org.mutabilitydetector.benchmarks.MutableByHavingProtectedNonFinalField;
import org.mutabilitydetector.benchmarks.MutableByHavingPublicNonFinalField;
import org.mutabilitydetector.checkers.IMutabilityChecker;
import org.mutabilitydetector.checkers.PublishedNonFinalFieldChecker;




public class PublishedNonFinalFieldCheckerTest {

	private IMutabilityChecker checker;
	
	@Before
	public void setUp() {
		checker = new PublishedNonFinalFieldChecker();
	}
	
	@Test
	public void testImmutableExamplePassesCheck() throws Exception {
		new CheckerRunner(null).run(checker, ImmutableExample.class);
		assertImmutable(checker.result());
		assertEquals(0, checker.reasons().size());
	}
	
	
	@Test
	public void testClassWithPublicNonFinalFieldFailsCheck() throws Exception {
		new CheckerRunner(null).run(checker, MutableByHavingPublicNonFinalField.class);
		assertDefinitelyNotImmutable(checker.result());
	}
	
	@Test
	public void testClassWithProtectedNonFinalFieldFailsCheck() throws Exception {
		new CheckerRunner(null).run(checker, MutableByHavingProtectedNonFinalField.class);
		assertDefinitelyNotImmutable(checker.result());
	}
	
	@Test
	public void testClassWithDefaultVisibleNonFinalFieldFailsCheck() throws Exception {
		new CheckerRunner(null).run(checker, MutableByHavingDefaultVisibleNonFinalField.class);
		assertDefinitelyNotImmutable(checker.result());
	}
	
	@Test
	public void testClassWithPublicFinalFieldPassesCheck() throws Exception {
		new CheckerRunner(null).run(checker, ImmutableWithPublicFinalField.class);
		assertImmutable(checker.result());
		assertEquals(0, checker.reasons().size());
	}
}
