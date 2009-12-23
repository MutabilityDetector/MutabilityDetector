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

import static org.junit.Assert.assertTrue;
import static org.mutabilitydetector.ImmutableAssert.assertDefinitelyNotImmutable;
import static org.mutabilitydetector.ImmutableAssert.assertImmutable;

import org.junit.Before;
import org.junit.Test;
import org.mutabilitydetector.CheckerRunner;
import org.mutabilitydetector.benchmarks.types.AbstractType;
import org.mutabilitydetector.benchmarks.types.ClassWithAllPrimitives;
import org.mutabilitydetector.benchmarks.types.EnumType;
import org.mutabilitydetector.benchmarks.types.InterfaceType;
import org.mutabilitydetector.checkers.IMutabilityChecker;
import org.mutabilitydetector.checkers.InherentTypeMutabilityChecker;



public class InherentTypeMutabilityCheckerTest {

	private IMutabilityChecker checker;

	@Before
	public void setUp() {
		checker = new InherentTypeMutabilityChecker();
	}

	@Test
	public void testAbstractTypesAreInherentlyMutable() throws Exception {
		new CheckerRunner(null).run(checker, AbstractType.class);
		assertDefinitelyNotImmutable(checker.result());
		assertTrue(checker.reasons().size() > 0);
	}
	
	
	@Test
	public void testEnumTypesAreInherentlyImmutable() throws Exception {
		new CheckerRunner(null).run(checker, EnumType.class);
		assertImmutable(checker.result());
	}
	
	@Test
	public void testInterfacesAreInherentlyMutable() throws Exception {
		new CheckerRunner(null).run(checker, InterfaceType.class);
		assertDefinitelyNotImmutable(checker.result());
		assertTrue(checker.reasons().size() > 0);
	}
	
	@Test
	public void testPrimitiveTypesAreInherentlyImmutable() throws Exception {
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

	@SuppressWarnings("unused")
	private void assertMutableClass(Class<?> toCheck) {
		new CheckerRunner(null).run(checker, toCheck);
		assertDefinitelyNotImmutable(checker.result());
		assertTrue(checker.reasons().size() > 0);
	}

	private void assertImmutableClass(Class<?> toCheck) {
		new CheckerRunner(null).run(checker, toCheck);
		assertImmutable(checker.result());
	}
}
