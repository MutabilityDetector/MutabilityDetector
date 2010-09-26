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

import java.util.Collection;

import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.CheckerReasonDetail;
import org.mutabilitydetector.IAnalysisSession;
import org.mutabilitydetector.IAnalysisSession.AnalysisResult;
import org.mutabilitydetector.IAnalysisSession.IsImmutable;

public class MutabilityAssert {

	private final static AssertionReporter reporter = new AssertionReporter();
	
	private static class AnalysisSessionHolder {
		static final IAnalysisSession assertionAnalysisSession = AnalysisSession.createWithCurrentClassPath();
	}
	
	public static void assertImmutable(Class<?> expectedImmutableClass) {
		String className = expectedImmutableClass.getName();
		AnalysisResult analysisResult = getResultFor(className);
		
		reporter.expectedImmutable(analysisResult);
	}

	private static AnalysisResult getResultFor(String className) {
		AnalysisResult analysisResult = AnalysisSessionHolder.assertionAnalysisSession.resultFor(className);
		return analysisResult;
	}

	public static String formatReasons(Collection<CheckerReasonDetail> reasons) {
		return reporter.formatReasons(reasons);
	}

	public static void assertImmutableStatusIs(IsImmutable expected, Class<?> forClass) {
		AnalysisResult analysisResult = getResultFor(forClass.getName());
		reporter.expectedIsImmutable(expected, analysisResult);
	}
	

}
