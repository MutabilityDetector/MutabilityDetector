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
package org.mutabilitydetector.benchmarks.inheritance;


import static org.mutabilitydetector.ImmutableAssert.assertDefinitelyNotImmutable;
import static org.mutabilitydetector.TestUtil.getResultOfAnalysis;

import org.junit.Test;
import org.mutabilitydetector.benchmarks.inheritance.ImmutableSubtypeOfMutableSupertype;
import org.mutabilitydetector.benchmarks.inheritance.MutableSubtypeOfMutableSupertype;
import org.mutabilitydetector.benchmarks.inheritance.MutableSupertype;


public class TestSubclassingOfImmutableType {

	@Test
	public void testSupertypeIsDefinitelyNotImmutable() throws Exception {
		assertDefinitelyNotImmutable(getResultOfAnalysis(MutableSupertype.class));
	}
	
	@Test
	public void testImmutableSubtypeIsReportedAsImmutable() throws Exception {
		assertDefinitelyNotImmutable(getResultOfAnalysis(ImmutableSubtypeOfMutableSupertype.class));
	}

	
	@Test
	public void testMutableSubtype() throws Exception {
		assertDefinitelyNotImmutable(getResultOfAnalysis(MutableSubtypeOfMutableSupertype.class));
	}
}
