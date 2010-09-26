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
import static org.mutabilitydetector.checkers.SetterMethodChecker.newSetterMethodChecker;

import java.math.BigDecimal;
import java.util.Collections;

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
import org.mutabilitydetector.checkers.SetterMethodChecker;
import org.mutabilitydetector.checkers.info.PrivateMethodInvocationInfo;

public class SetterMethodCheckerTest {

	private SetterMethodChecker checker;
	private CheckerRunner checkerRunner;
	
	
	@Test public void immutableExamplePassesCheck() throws Exception {
		doCheck(ImmutableExample.class);

		assertImmutable(checker.result());
		assertEquals(checker.reasons().size(), 0);
	}
	
	@Test public void mutableByHavingSetterMethodFailsCheck() throws Exception {
		doCheck(MutableByHavingSetterMethod.class);

		assertDefinitelyNotImmutable(checker.result());
	}
	
	@Test public void integerClassPassesCheck() throws Exception {
		doCheck(Integer.class);

		assertEquals(Collections.EMPTY_LIST, checker.reasons());
		assertImmutable(checker.result());
	}
	
	@Test public void enumTypePassesCheck() throws Exception {
		doCheck(EnumType.class);

		assertEquals(Collections.EMPTY_LIST, checker.reasons());
		assertImmutable(checker.result());
	}
	
	@Test public void settingFieldOfOtherInstanceDoesNotRenderClassMutable() throws Exception {
		doCheck(ImmutableButSetsPrivateFieldOfInstanceOfSelf.class);
		assertIsImmutable();
	}
	
	@Test public void fieldsSetInPrivateMethodCalledOnlyFromConstructorIsImmutable() {
		doCheck(ImmutableUsingPrivateFieldSettingMethod.class);
		assertIsImmutable();
	}

	@Test public void settingFieldOfObjectPassedAsParameterDoesNotRenderClassMutable() throws Exception {
		doCheck(ImmutableButSetsFieldOfOtherClass.class);
		assertIsImmutable();
	}
	
	@Test public void settingFieldOfMutableFieldRendersClassMutable() throws Exception {
		doCheck(MutableBySettingFieldOfField.class);
		assertDefinitelyNotImmutable(checker.result());
	}

	@Test public void subclassOfSettingFieldOfMutableFieldRendersClassMutable() throws Exception {
		doCheck(StillMutableSubclass.class);
		assertDefinitelyNotImmutable(checker.result());
	}

	@Test public void bigDecimalDoesNotFailCheck() throws Exception {
		doCheck(BigDecimal.class);
		assertIsImmutable();
	}
	
	@Test public void stringDoesNotFailCheck() throws Exception {
		doCheck(String.class);
		assertIsImmutable();
	}

	@Test public void fieldReassignmentInPublicStaticMethodMakesClassMutable() throws Exception {
		doCheck(MutableByAssigningFieldOnInstanceWithinStaticMethod.class);
		assertDefinitelyNotImmutable(checker.result());
	}

	@Test public void reassignmentOfStackConfinedObjectDoesNotFailCheck() throws Exception {
		doCheck(ImmutableWithMutatingStaticFactoryMethod.class);
		assertIsImmutable();
	}

	private void assertIsImmutable() {
		assertEquals(TestUtil.formatReasons(checker.reasons()), Collections.EMPTY_LIST, checker.reasons());
	}

	private void doCheck(Class<?> toCheck) {
		checkerRunner = CheckerRunner.createWithCurrentClasspath();
		PrivateMethodInvocationInfo info = new PrivateMethodInvocationInfo(checkerRunner);
		checker = newSetterMethodChecker(info);
		checkerRunner.run(checker, toCheck);
	}
}
