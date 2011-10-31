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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mutabilitydetector.ImmutableAssert.assertDefinitelyNotImmutable;
import static org.mutabilitydetector.ImmutableAssert.assertEffectivelyImmutable;
import static org.mutabilitydetector.ImmutableAssert.assertImmutable;
import static org.mutabilitydetector.TestMatchers.hasReasons;
import static org.mutabilitydetector.TestUtil.runChecker;

import org.junit.Before;
import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.benchmarks.types.AbstractType;
import org.mutabilitydetector.benchmarks.types.ClassWithAllPrimitives;
import org.mutabilitydetector.benchmarks.types.EnumType;
import org.mutabilitydetector.benchmarks.types.InterfaceType;
import org.mutabilitydetector.checkers.IMutabilityChecker;
import org.mutabilitydetector.checkers.InherentTypeMutabilityChecker;
import org.mutabilitydetector.locations.ClassLocation;
import org.mutabilitydetector.locations.FieldLocation;



public class InherentTypeMutabilityCheckerTest {

	private IMutabilityChecker checker;
	private AnalysisResult result;

	@Before public void setUp() {
		checker = new InherentTypeMutabilityChecker();
	}

	@Test public void abstractTypesAreInherentlyMutable() throws Exception {
		result = runChecker(checker, AbstractType.class);

		assertThat(checker, hasReasons());
		assertDefinitelyNotImmutable(result);
	}
	
	
	@Test public void enumTypesAreInherentlyImmutable() throws Exception {
		result = runChecker(checker, EnumType.class);
		
		assertImmutable(result);
	}
	
	@Test public void interfacesAreInherentlyMutable() throws Exception {
		result = runChecker(checker, InterfaceType.class);
		
		assertThat(checker, hasReasons());
		assertDefinitelyNotImmutable(result);
	}
	
	@Test public void primitiveTypesAreInherentlyImmutable() throws Exception {
		assertImmutableClass(ClassWithAllPrimitives.Boolean.class);
		assertImmutableClass(ClassWithAllPrimitives.Byte.class);
		assertImmutableClass(ClassWithAllPrimitives.Char.class);
		assertImmutableClass(ClassWithAllPrimitives.Short.class);
		assertImmutableClass(ClassWithAllPrimitives.Int.class);
		assertImmutableClass(ClassWithAllPrimitives.Long.class);
		assertImmutableClass(ClassWithAllPrimitives.Float.class);
		assertImmutableClass(ClassWithAllPrimitives.Double.class);
		/* @link InherentTypeMutabilityChecker#visitField(int, String, String, String, Object) 
		assertMutableClass(ClassWithAllPrimitives.Array.class); */ 
	}
	
	@Test public void arrayTypesAreInherentlyMutable() throws Exception {
		result = runChecker(checker, ClassWithAllPrimitives.Array.class);
		
		assertThat(checker, hasReasons());
		assertEffectivelyImmutable(result);
	}
	
	@Test public void arrayFieldWhichIsStaticAllowsClassToRemainImmutable() throws Exception {
		result = runChecker(checker, ImmutableWhenArrayFieldIsStatic.class);
		
		assertImmutable(result);
	}
	
	@Test
	public void arrayFieldCodeLocationIsFieldLocationWithNameOfField() throws Exception {
		runChecker(checker,  ClassWithAllPrimitives.Array.class);
		FieldLocation sourceLocation = (FieldLocation) checker.reasons().iterator().next().codeLocation();
		
		assertThat(sourceLocation.typeName(), is(ClassWithAllPrimitives.Array.class.getName()));
		assertThat(sourceLocation.fieldName(), is("anArray"));
	}
	
	@Test
	public void codeLocationOfAbstractTypeIsClassLocationWithNameOfClass() throws Exception {
		runChecker(checker, AbstractType.class);
		ClassLocation codeLocation = (ClassLocation) checker.reasons().iterator().next().codeLocation();
		
		assertThat(codeLocation.typeName(), is(AbstractType.class.getName()));
	}

	private void assertImmutableClass(Class<?> toCheck) {
		assertImmutable(runChecker(checker, toCheck));
	}
}
