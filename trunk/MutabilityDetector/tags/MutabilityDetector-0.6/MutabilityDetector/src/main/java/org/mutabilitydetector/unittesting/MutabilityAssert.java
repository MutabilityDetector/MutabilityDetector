/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector.unittesting;

import static org.mutabilitydetector.unittesting.AnalysisSessionHolder.analysisResultFor;
import static org.mutabilitydetector.unittesting.matchers.WithAllowedReasonsMatcher.withAllowedReasons;

import java.util.Collection;

import org.hamcrest.MatcherAssert;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.CheckerReasonDetail;
import org.mutabilitydetector.IAnalysisSession.IsImmutable;
import org.mutabilitydetector.unittesting.matchers.AnalysisResultMatcher;
import org.mutabilitydetector.unittesting.matchers.IsImmutableMatcher;
import org.mutabilitydetector.unittesting.matchers.WithAllowedReasonsMatcher;

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
	
	public static void assertInstancesOf(Class<?> clazz, IsImmutableMatcher areImmutable) {
		MatcherAssert.assertThat(getResultFor(clazz), areImmutable);
	}

	public static void assertInstancesOf(Class<?> clazz, IsImmutableMatcher areImmutable, AnalysisResultMatcher allowing) {
		WithAllowedReasonsMatcher areImmutable_withReasons = withAllowedReasons(areImmutable, allowing);
		MatcherAssert.assertThat(getResultFor(clazz), areImmutable_withReasons);
		
	}

}
