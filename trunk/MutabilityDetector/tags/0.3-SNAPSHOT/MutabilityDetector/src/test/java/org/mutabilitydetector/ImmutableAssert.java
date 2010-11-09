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

import org.mutabilitydetector.IAnalysisSession.AnalysisError;
import org.mutabilitydetector.IAnalysisSession.IsImmutable;

public class ImmutableAssert {

	public static void assertImmutable(IsImmutable result) {
		String failString = "Expected Immutable result.%n";
		assertEquals(failString, IsImmutable.DEFINITELY, result);
	}
	
	public static void assertImmutable(AnalysisResult result) {
		String failString = "Expected Immutable result.%n";
		failString += result.reasons.toString();
		assertEquals(failString, IsImmutable.DEFINITELY, result.isImmutable);
	}
	
	public static void assertDefinitelyNotImmutable(IsImmutable result) {
		assertEquals("Expected Not Immutable result.", IsImmutable.DEFINITELY_NOT, result);
	}

	public static void assertDefinitelyNotImmutable(AnalysisResult result) {
		assertEquals("Expected Not Immutable result.", IsImmutable.DEFINITELY_NOT, result.isImmutable);
	}
	
	public static void assertNotImmutable(IsImmutable result) {
		String error = "Expected any result but Immutable. %nActual: " + result.name();
		assertFalse(error, IsImmutable.DEFINITELY.equals(result));
		
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

	private static void doAssertion(Class<?> toAnalyse, IsImmutable expected, boolean printReasons) {
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
}
