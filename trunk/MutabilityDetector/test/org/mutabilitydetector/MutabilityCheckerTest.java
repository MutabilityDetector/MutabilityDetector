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
package org.mutabilitydetector;

import static org.mutabilitydetector.ImmutableAssert.assertImmutable;
import static org.mutabilitydetector.ImmutableAssert.assertMaybeImmutable;
import static org.mutabilitydetector.ImmutableAssert.assertNotImmutable;


import org.junit.Test;
import org.mutabilitydetector.benchmarks.ImmutableExample;
import org.mutabilitydetector.benchmarks.MutableByAssigningInterfaceToField;
import org.mutabilitydetector.benchmarks.MutableByHavingMutableFieldAssigned;
import org.mutabilitydetector.benchmarks.MutableByNoCopyOfIndirectlyConstructedField;
import org.mutabilitydetector.benchmarks.MutableByNotBeingFinalClass;
import org.mutabilitydetector.benchmarks.settermethod.MutableByHavingSetterMethod;
import org.mutabilitydetector.benchmarks.types.EnumType;

/**
 * This test acts as an overall progress checker as well as a general acceptance
 * of the tool so far. There are several classes used as micro benchmarks to
 * tell if the tool is correct. Once the checker can correctly assess these
 * classes the tool is correct for our definition.
 * 
 * The pattern seems to be that either all the classes which are mutable pass
 * the checks, and the single immutable one doesn't. Or the other way round. The
 * tool won't be correct until every check passes.
 * 
 * @author graham
 * 
 */
public class MutabilityCheckerTest {

	@Test
	public void testImmutableExample() throws Exception {
		assertImmutable(ImmutableExample.class);
	}

	@Test
	public void testMutableByAssigningAbstractTypeToField() throws Exception {
		assertNotImmutable(MutableByAssigningInterfaceToField.class);
	}

	@Test
	public void testMutableByHavingMutableFieldAssigned() throws Exception {
		assertNotImmutable(MutableByHavingMutableFieldAssigned.class);
	}

	@Test
	public void testMutableByHavingSetterMethod() throws Exception {
		assertNotImmutable(MutableByHavingSetterMethod.class);
	}

	@Test
	public void testMutableByNoCopyOfIndirectlyConstructedField() throws Exception {
		assertNotImmutable(MutableByNoCopyOfIndirectlyConstructedField.class);
	}

	@Test
	public void testMutableByNotBeingFinalClass() throws Exception {
		assertMaybeImmutable(MutableByNotBeingFinalClass.class);
	}

	@Test
	public void testEnumTypesAreImmutable() throws Exception {
		assertImmutable(EnumType.class);
	}

	@Test
	public void testIntegerIsImmutable() throws Exception {
		assertImmutable(Integer.class);
	}

}
