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
import static org.mutabilitydetector.ImmutableAssert.assertDefinitelyNotImmutable;
import static org.mutabilitydetector.ImmutableAssert.assertImmutable;

import java.util.Collections;

import org.junit.Test;
import org.mutabilitydetector.CheckerRunner;
import org.mutabilitydetector.benchmarks.types.EnumType;
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
	
	@Test
	public void testEnumTypePassesCheck() throws Exception {
		checker = new SetterMethodChecker();
		new CheckerRunner(null).run(checker, EnumType.class);
		
		assertEquals(Collections.EMPTY_LIST, checker.reasons());
		assertImmutable(checker.result());
	}
}
