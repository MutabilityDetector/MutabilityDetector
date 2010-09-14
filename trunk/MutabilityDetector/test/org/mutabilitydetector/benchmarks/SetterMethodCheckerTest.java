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

import java.math.BigDecimal;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mutabilitydetector.CheckerRunner;
import org.mutabilitydetector.TestUtil;
import org.mutabilitydetector.benchmarks.settermethod.ImmutableButSetsFieldOfOtherClass;
import org.mutabilitydetector.benchmarks.settermethod.ImmutableButSetsPrivateFieldOfInstanceOfSelf;
import org.mutabilitydetector.benchmarks.settermethod.ImmutableUsingPrivateFieldSettingMethod;
import org.mutabilitydetector.benchmarks.settermethod.ImmutableWithMutatingStaticFactoryMethod;
import org.mutabilitydetector.benchmarks.settermethod.MutableByAssigningFieldOnInstanceWithinStaticMethod;
import org.mutabilitydetector.benchmarks.settermethod.MutableByHavingSetterMethod;
import org.mutabilitydetector.benchmarks.settermethod.MutableBySettingFieldOfField;
import org.mutabilitydetector.benchmarks.settermethod.StillMutableSubclass;
import org.mutabilitydetector.benchmarks.types.EnumType;
import org.mutabilitydetector.checkers.IMutabilityChecker;
import org.mutabilitydetector.checkers.SetterMethodChecker;

public class SetterMethodCheckerTest {

	private SetterMethodChecker checker;
	
	@Before
	public void setUp() {
		checker = new SetterMethodChecker();
		
	}
	
	@Test
	public void testImmutableExamplePassesCheck() throws Exception {
		doCheck(checker, ImmutableExample.class);
		
		assertImmutable(checker.result());
		assertEquals(checker.reasons().size(), 0);
	}
	
	@Test
	public void testMutableByHavingSetterMethodFailsCheck() throws Exception {
		doCheck(checker, MutableByHavingSetterMethod.class);
		
		assertDefinitelyNotImmutable(checker.result());
	}
	
	@Test
	public void testIntegerClassPassesCheck() throws Exception {
		doCheck(checker, Integer.class);
		
		assertEquals(Collections.EMPTY_LIST, checker.reasons());
		assertImmutable(checker.result());
	}
	
	@Test
	public void testEnumTypePassesCheck() throws Exception {
		checker = new SetterMethodChecker();
		doCheck(checker, EnumType.class);
		
		assertEquals(Collections.EMPTY_LIST, checker.reasons());
		assertImmutable(checker.result());
	}
	
	@Test
	public void testSettingFieldOfOtherInstanceDoesNotRenderClassMutable() throws Exception {
		doCheck(checker, ImmutableButSetsPrivateFieldOfInstanceOfSelf.class);
		assertIsImmutable();
	}
	
	@Test
	public void testFieldsSetInPrivateMethodCalledOnlyFromConstructorIsImmutable() {
		doCheck(checker, ImmutableUsingPrivateFieldSettingMethod.class);
		assertIsImmutable();
	}
	
	@Test
	public void testSettingFieldOfObjectPassedAsParameterDoesNotRenderClassMutable() throws Exception {
		doCheck(checker, ImmutableButSetsFieldOfOtherClass.class);
		assertIsImmutable();
	}
	
	@Test
	public void testSettingFieldOfMutableFieldRendersClassMutable() throws Exception {
		doCheck(checker, MutableBySettingFieldOfField.class);
		assertDefinitelyNotImmutable(checker.result());
	}
	
	@Test
	public void testSubclassOfSettingFieldOfMutableFieldRendersClassMutable() throws Exception {
		doCheck(checker, StillMutableSubclass.class);
		assertDefinitelyNotImmutable(checker.result());
	}
	
	@Test
	public void testBigDecimalDoesNotFailCheck() throws Exception {
		doCheck(checker, BigDecimal.class);
		assertIsImmutable();
	}
	
	@Test
	public void testStringDoesNotFailCheck() throws Exception {
		doCheck(checker, String.class);
		assertIsImmutable();
	}
	
	@Test
	public void testFieldReassignmentInPublicStaticMethodMakesClassMutable() throws Exception {
		doCheck(checker, MutableByAssigningFieldOnInstanceWithinStaticMethod.class);
		assertDefinitelyNotImmutable(checker.result());
	}
	
	@Test
	public void testReassignmentOfStackConfinedObjectDoesNotFailCheck() throws Exception {
		doCheck(checker, ImmutableWithMutatingStaticFactoryMethod.class);
		assertIsImmutable();
	}
	
	private void assertIsImmutable() {
		assertEquals(TestUtil.formatReasons(checker.reasons()), Collections.EMPTY_LIST, checker.reasons());
	}

	private void doCheck(IMutabilityChecker checkerToRun, Class<?> toCheck) {
		new CheckerRunner(null).run(checkerToRun, toCheck);
	}
}
