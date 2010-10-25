/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector.junit;

import static org.mutabilitydetector.IAnalysisSession.IsImmutable.DEFINITELY_NOT;
import static org.mutabilitydetector.junit.AnalysisSessionHolder.analysisResultFor;

import java.util.Collection;

import org.mutabilitydetector.CheckerReasonDetail;
import org.mutabilitydetector.IAnalysisSession.AnalysisResult;
import org.mutabilitydetector.IAnalysisSession.IsImmutable;

public class MutabilityAssert {

	private final static AssertionReporter reporter = new AssertionReporter();
	
	public static void assertImmutable(Class<?> expectedImmutableClass) {
		reporter.expectedImmutable(getResultFor(expectedImmutableClass));
	}

	private static AnalysisResult getResultFor(Class<?> clazz) {
		return analysisResultFor(clazz);
	}

	public static String formatReasons(Collection<CheckerReasonDetail> reasons) {
		return reporter.formatReasons(reasons);
	}

	public static void assertImmutableStatusIs(IsImmutable expected, Class<?> forClass) {
		AnalysisResult analysisResult = getResultFor(forClass);
		reporter.expectedIsImmutable(expected, analysisResult);
	}
	
	public static class SingleMutabilityAssert {
		
		private final Class<?> clazz;

		public SingleMutabilityAssert(Class<?> clazz) {
			this.clazz = clazz;
		}
		
		public void isImmutable() {
			assertImmutable(clazz);
		}

		public void isNotImmutable() {
			assertImmutableStatusIs(DEFINITELY_NOT, clazz);
		}
	}

	public static SingleMutabilityAssert assertThat(Class<?> clazz) {
		return new SingleMutabilityAssert(clazz);
	}

}
