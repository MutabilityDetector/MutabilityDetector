/*
 * Mutability Detector
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * 
 * Further licensing information for this project can be found in
 * license/LICENSE.txt
 */

package org.mutabilitydetector.unittesting;

import static java.lang.String.format;
import static org.mutabilitydetector.IAnalysisSession.IsImmutable.DEFINITELY;
import static org.mutabilitydetector.unittesting.matchers.MutabilityMatchers.noWarningsAllowed;

import java.util.Collection;

import org.hamcrest.Matcher;
import org.mutabilitydetector.CheckerReasonDetail;
import org.mutabilitydetector.IAnalysisSession.AnalysisResult;
import org.mutabilitydetector.IAnalysisSession.IsImmutable;

/**
 * {@link AssertionReporter} is responsible for making an assertion in a test fail, by
 * preparing and throwing the appropriate {@link MutabilityAssertionError}.
 */
public class AssertionReporter {

	public void expectedImmutable(AnalysisResult analysisResult) {
		expectedIsImmutable(DEFINITELY, analysisResult, noWarningsAllowed());
	}

	public String formatReasons(Collection<CheckerReasonDetail> reasons) {
		return formatReasons(reasons, new StringBuilder());
	}
	
	private static String formatReasons(Collection<CheckerReasonDetail> reasons, StringBuilder builder) {
		builder.append(format("    Reasons:%n"));
		for(CheckerReasonDetail reason: reasons) {
			builder.append(format("        %s%n", reason.message()));
		}
		return builder.toString();
	}

	public void expectedIsImmutable(IsImmutable expected, AnalysisResult analysisResult) {
		this.expectedIsImmutable(expected, analysisResult, noWarningsAllowed());
	}

	private String buildExpectedIsImmutableExceptionMessage(IsImmutable expected, AnalysisResult analysisResult) {
		StringBuilder messageBuilder = new StringBuilder();
		messageBuilder.append(format("Expected class %s to be [%s] immutable, but was [%s] immutable.%n", 
				analysisResult.dottedClassName, expected, analysisResult.isImmutable));
		formatReasons(analysisResult.reasons, messageBuilder);
		return messageBuilder.toString();
	}

	public void expectedIsImmutable(IsImmutable expected, AnalysisResult analysisResult, Matcher<AnalysisResult> allowed) {
		if(gotTheExpectedResult(expected, analysisResult) 
			|| mutabilityReasonsHaveBeenSuppressed(analysisResult, allowed)) {
			return; 
		} else {
			reportAssertionError(expected, analysisResult);
		}
	}

	private void reportAssertionError(IsImmutable expected, AnalysisResult analysisResult) {
		String message = buildExpectedIsImmutableExceptionMessage(expected, analysisResult);
		throw new MutabilityAssertionError(message);
	}

	private boolean mutabilityReasonsHaveBeenSuppressed(AnalysisResult analysisResult, Matcher<AnalysisResult> allowed) {
		return allowed.matches(analysisResult);
	}

	private boolean gotTheExpectedResult(IsImmutable expected, AnalysisResult analysisResult) {
		return expected == analysisResult.isImmutable;
	}

}
