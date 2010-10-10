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

import java.lang.reflect.Array;
import java.util.Date;

import org.junit.Test;
import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.IAnalysisSession;
import org.mutabilitydetector.TestUtil;
import org.mutabilitydetector.IAnalysisSession.AnalysisError;
import org.mutabilitydetector.IAnalysisSession.AnalysisResult;
import org.mutabilitydetector.IAnalysisSession.IsImmutable;
import org.mutabilitydetector.benchmarks.settermethod.MutableByHavingSetterMethod;
import org.mutabilitydetector.benchmarks.types.EnumType;

/**
 * This test acts as an overall progress checker as well as a general acceptance
 * of the tool so far. There are several classes used as micro benchmarks to
 * tell if the tool is correct. Once the checker can correctly assess these
 * classes the tool is correct for our definition.
 * 
 * The rules for defining the tool as correct can be found at {@link https
 * ://devweb2009.cis.strath.ac.uk/trac/softeval0/wiki/RulesForImmutability }
 * 
 * The pattern seems to be that either all the classes which are mutable pass
 * the checks, and the single immutable one doesn't. Or the other way round. The
 * tool won't be correct until every check passes.
 * 
 * @author graham
 * 
 */
public class MutabilityCheckerTest {

	private void assertNotImmutable(Class<?> toAnalyse) {
		doAssertion(toAnalyse, IsImmutable.DEFINITELY_NOT, true);
	}

	private void assertImmutable(Class<?> toAnalyse) {
		doAssertion(toAnalyse, IsImmutable.DEFINITELY, true);
	}

	private void assertMaybeImmutable(Class<?> toAnalyse) {
		doAssertion(toAnalyse, IsImmutable.MAYBE, true);
	}

	private void doAssertion(Class<?> toAnalyse, IsImmutable expected, boolean printReasons) {
		IAnalysisSession session = AnalysisSession.createWithCurrentClassPath();
		AnalysisResult result = session.resultFor(toAnalyse.getName());
		
		if(printReasons) {
			for (AnalysisError error : session.getErrors()) {
				System.err.printf("Analysis error running checker=[%s] on class=[%s.class]:%n%s%n", 
								      error.checkerName, error.onClass, error.description);
			}
		}
		String failure = "Exception " + toAnalyse.getName() + " is expected to be " + expected + " immutable.";
		if (printReasons) {
			failure += TestUtil.formatReasons(result.reasons);
		}
		assertEquals(failure, expected, result.isImmutable);

	}

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
	public void testWellKnownJavaTypes() throws Exception {
		assertImmutable(Integer.class);
		assertImmutable(int.class);
		assertImmutable(Array.class);
		// the hash code field is lazily computed, and renders String mutable
		// assertImmutable(String.class);
		assertMaybeImmutable(Object.class);
		assertNotImmutable(Date.class);

	}

	@Test
	public void testIntegerIsImmutable() throws Exception {
		assertImmutable(Integer.class);
	}

}
