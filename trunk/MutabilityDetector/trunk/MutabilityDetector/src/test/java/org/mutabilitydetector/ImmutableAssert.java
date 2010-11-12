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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mutabilitydetector.IAnalysisSession.IsImmutable.DEFINITELY;
import static org.mutabilitydetector.IAnalysisSession.IsImmutable.DEFINITELY_NOT;
import static org.mutabilitydetector.IAnalysisSession.IsImmutable.MAYBE;
import static org.mutabilitydetector.TestUtil.formatReasons;

import org.mutabilitydetector.IAnalysisSession.AnalysisError;
import org.mutabilitydetector.IAnalysisSession.IsImmutable;

public class ImmutableAssert {

	public static void assertImmutable(IsImmutable result) {
		String failString = "Expected Immutable result.%n";
		assertEquals(failString, IsImmutable.DEFINITELY, result);
	}
	
	public static void assertImmutable(AnalysisResult result) {
		doAssertion(result.dottedClassName, DEFINITELY, result, true);
	}
	
	public static void assertDefinitelyNotImmutable(IsImmutable result) {
		assertEquals("Expected Not Immutable result.", IsImmutable.DEFINITELY_NOT, result);
	}

	public static void assertDefinitelyNotImmutable(AnalysisResult result) {
		doAssertion(result.dottedClassName, DEFINITELY_NOT, result, true);
	}
	
	public static void assertNotImmutable(IsImmutable result) {
		String error = "Expected any result but Immutable. %nActual: " + result.name();
		assertFalse(error, IsImmutable.DEFINITELY.equals(result));
	}
	
	public static void assertNotImmutable(AnalysisResult result) {
		doAssertion(result.dottedClassName, DEFINITELY_NOT, result, true);
	}
	
	public static void assertIsImmutableResult(IsImmutable expected, IsImmutable actual) {
		assertEquals(expected, actual);
	}
	
	public static void assertIsImmutableResult(IsImmutable expected, AnalysisResult actualResult) {
		String error = actualResult.reasons.toString();
		assertEquals(error, expected, actualResult.isImmutable);
		
	}
	
	public static void assertNotImmutable(Class<?> toAnalyse) {
		doAssertion(toAnalyse, IsImmutable.DEFINITELY_NOT, true);
	}

	public static void assertImmutable(Class<?> toAnalyse) {
		doAssertion(toAnalyse, IsImmutable.DEFINITELY, true);
	}

	public static void assertMaybeImmutable(Class<?> toAnalyse) {
		doAssertion(toAnalyse, IsImmutable.MAYBE, true);
	}
	
	public static void assertMaybeImmutable(AnalysisResult result) {
		doAssertion(result.dottedClassName, MAYBE, result, true);
	}

	private static void doAssertion(Class<?> toAnalyse, IsImmutable expected, boolean printReasons) {
		IAnalysisSession session = AnalysisSession.createWithCurrentClassPath();
		String className = toAnalyse.getName();
		AnalysisResult result = session.resultFor(className);
		
		if(printReasons) {
			for (AnalysisError error : session.getErrors()) {
				System.err.printf("Analysis error running checker=[%s] on class=[%s.class]:%n%s%n", 
								      error.checkerName, error.onClass, error.description);
			}
		}
		
		doAssertion(className, expected, result, printReasons);

	}

	private static void doAssertion(String className, IsImmutable expected, AnalysisResult actual, boolean printReasons) {
		String failure = "Exception " + className + " is expected to be " + expected + " immutable.";
		if (printReasons) {
			failure += "\n" + formatReasons(actual.reasons);
		}
		assertEquals(failure, expected, actual.isImmutable);
	}


	
	
}
