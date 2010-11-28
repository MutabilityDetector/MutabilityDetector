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
package org.mutabilitydetector.benchmarks.settermethod;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mutabilitydetector.AnalysisSession.createWithCurrentClassPath;
import static org.mutabilitydetector.ImmutableAssert.assertDefinitelyNotImmutable;
import static org.mutabilitydetector.ImmutableAssert.assertImmutable;
import static org.mutabilitydetector.TestUtil.runChecker;
import static org.mutabilitydetector.checkers.SetterMethodChecker.newSetterMethodChecker;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.CheckerRunner;
import org.mutabilitydetector.IAnalysisSession;
import org.mutabilitydetector.TestUtil;
import org.mutabilitydetector.benchmarks.ImmutableExample;
import org.mutabilitydetector.benchmarks.settermethod.SetsFieldsOfDifferentTypes.SetsBoolean;
import org.mutabilitydetector.benchmarks.settermethod.SetsFieldsOfDifferentTypes.SetsByte;
import org.mutabilitydetector.benchmarks.settermethod.SetsFieldsOfDifferentTypes.SetsChar;
import org.mutabilitydetector.benchmarks.settermethod.SetsFieldsOfDifferentTypes.SetsDouble;
import org.mutabilitydetector.benchmarks.settermethod.SetsFieldsOfDifferentTypes.SetsFloat;
import org.mutabilitydetector.benchmarks.settermethod.SetsFieldsOfDifferentTypes.SetsInt;
import org.mutabilitydetector.benchmarks.settermethod.SetsFieldsOfDifferentTypes.SetsLong;
import org.mutabilitydetector.benchmarks.settermethod.SetsFieldsOfDifferentTypes.SetsObjectArray;
import org.mutabilitydetector.benchmarks.settermethod.SetsFieldsOfDifferentTypes.SetsObjectArrayArray;
import org.mutabilitydetector.benchmarks.settermethod.SetsFieldsOfDifferentTypes.SetsReference;
import org.mutabilitydetector.benchmarks.settermethod.SetsFieldsOfDifferentTypes.SetsShort;
import org.mutabilitydetector.benchmarks.types.EnumType;
import org.mutabilitydetector.checkers.SetterMethodChecker;
import org.mutabilitydetector.checkers.info.PrivateMethodInvocationInformation;
import org.mutabilitydetector.checkers.info.SessionCheckerRunner;


@RunWith(Theories.class)
public class SetterMethodCheckerTest {

	private SetterMethodChecker checker;
	private CheckerRunner checkerRunner;
	private IAnalysisSession analysisSession;
	private PrivateMethodInvocationInformation info;
	
	@Before public void setUp() {
		checkerRunner = CheckerRunner.createWithCurrentClasspath();
		analysisSession = createWithCurrentClassPath();
		info = new PrivateMethodInvocationInformation(new SessionCheckerRunner(analysisSession, checkerRunner));
		checker = newSetterMethodChecker(info);
	}
	
	private AnalysisResult doCheck(Class<?> toCheck) {
		return TestUtil.runChecker(checker, toCheck);
	}
	
	@Test public void immutableExamplePassesCheck() throws Exception {
		assertImmutable(doCheck(ImmutableExample.class));
	}
	
	@Test public void mutableByHavingSetterMethodFailsCheck() throws Exception {
		assertDefinitelyNotImmutable(doCheck(MutableByHavingSetterMethod.class));
	}
	
	@Test public void integerClassPassesCheck() throws Exception {
		assertImmutable(doCheck(Integer.class));
	}
	
	@Test public void enumTypePassesCheck() throws Exception {
		assertImmutable(doCheck(EnumType.class));
	}
	
	@Ignore("Field [myField] can be reassigned within method [setPrivateFieldOnInstanceOfSelf]" + 
			"Field [primitiveField] can be reassigned within method [setPrivateFieldOnInstanceOfSelf]")
	@Test public void settingFieldOfOtherInstanceDoesNotRenderClassMutable() throws Exception {
		assertImmutable(doCheck(ImmutableButSetsPrivateFieldOfInstanceOfSelf.class));
	}
	
	@Test public void settingFieldOfOtherInstanceAndThisInstanceRendersClassMutable() throws Exception {
		assertDefinitelyNotImmutable(doCheck(MutableBySettingFieldOnThisInstanceAndOtherInstance.class));
	}
	
	@Test public void fieldsSetInPrivateMethodCalledOnlyFromConstructorIsImmutable() {
		assertImmutable(doCheck(ImmutableUsingPrivateFieldSettingMethod.class));
	}

	@Ignore("Field [reassignable] can be reassigned within method [setFieldOnParameter]")
	@Test public void settingFieldOfObjectPassedAsParameterDoesNotRenderClassMutable() throws Exception {
		assertImmutable(doCheck(ImmutableButSetsFieldOfOtherClass.class));
	}
	
	@Ignore("Was <DEFINITELY> immutable")
	@Test public void settingFieldOfMutableFieldRendersClassMutable() throws Exception {
		assertDefinitelyNotImmutable(doCheck(MutableBySettingFieldOfField.class));
	}

	@Ignore("Does not create any reasons.")
	@Test public void subclassOfSettingFieldOfMutableFieldRendersClassMutable() throws Exception {
		AnalysisResult result = doCheck(StillMutableSubclass.class);
		
		assertThat(checker.reasons().size(), is(not(0)));
		assertDefinitelyNotImmutable(result);
	}
	
	@Ignore("Field [precision] can be reassigned within method [precision]" + 
			"Field [stringCache] can be reassigned within method [toString]" + 
			"Field [intVal] can be reassigned within method [inflate]")
	@Test public void bigDecimalDoesNotFailCheck() throws Exception {
		assertImmutable(doCheck(BigDecimal.class));
	}
	
	@Ignore("Field [hash] can be reassigned within method [hashCode]")
	@Test public void stringDoesNotFailCheck() throws Exception {
		assertImmutable(doCheck(String.class));
	}

	@Test public void fieldReassignmentInPublicStaticMethodMakesClassMutable() throws Exception {
		AnalysisResult result = doCheck(MutableByAssigningFieldOnInstanceWithinStaticMethod.class);
		assertDefinitelyNotImmutable(result);
	}

	@Ignore("Field can be reassigned.")
	@Test public void reassignmentOfStackConfinedObjectDoesNotFailCheck() throws Exception {
		assertImmutable(doCheck(ImmutableWithMutatingStaticFactoryMethod.class));
	}
	
	@DataPoints public static Class<?>[] classes = new Class[] {
			SetsBoolean.class,
			SetsByte.class,
			SetsChar.class,
			SetsDouble.class,
			SetsFloat.class,
			SetsInt.class,
			SetsLong.class,
			SetsObjectArray.class,
			SetsObjectArrayArray.class,
			SetsReference.class,
			SetsShort.class
	};
	
	
	
	@Theory public void 
	settingFieldsOfAnyTypeShouldBeMutable(Class<?> mutableSettingField) throws Exception {
		AnalysisResult result = runChecker(checker, mutableSettingField);
		assertDefinitelyNotImmutable(result);
	}

}
