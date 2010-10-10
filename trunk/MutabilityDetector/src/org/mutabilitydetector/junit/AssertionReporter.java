/*
 * Mutability Detector
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * 
 * Further licensing information for this project can be found in
 * license/LICENSE.txt
 */

package org.mutabilitydetector.junit;

import static java.lang.String.format;
import static org.mutabilitydetector.IAnalysisSession.IsImmutable.DEFINITELY;

import java.util.Collection;

import org.mutabilitydetector.CheckerReasonDetail;
import org.mutabilitydetector.IAnalysisSession.AnalysisResult;
import org.mutabilitydetector.IAnalysisSession.IsImmutable;

/**
 * {@link AssertionReporter} is responsible for making an assertion in a test fail, by
 * preparing and throwing the appropriate {@link MutabilityAssertionError}.
 */
public class AssertionReporter {

	public void expectedImmutable(AnalysisResult analysisResult) {
		if(analysisResult.isImmutable != IsImmutable.DEFINITELY) {
			String message = buildExpectedImmutableExceptionMessage(analysisResult);
			throw new MutabilityAssertionError(message);
		}
	}

	private String buildExpectedImmutableExceptionMessage(AnalysisResult analysisResult) {
		return buildExpectedIsImmutableExceptionMessage(DEFINITELY, analysisResult);
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

	public void expectedIsImmutable(IsImmutable expected , AnalysisResult analysisResult) {
		if(expected != analysisResult.isImmutable) {
			String message = buildExpectedIsImmutableExceptionMessage(expected, analysisResult);
			throw new MutabilityAssertionError(message);
		}
	}

	private String buildExpectedIsImmutableExceptionMessage(IsImmutable expected, AnalysisResult analysisResult) {
		StringBuilder messageBuilder = new StringBuilder();
		messageBuilder.append(format("Expected class %s to be [%s] immutable, but was [%s] immutable.%n", 
				analysisResult.dottedClassName, expected, analysisResult.isImmutable));
		formatReasons(analysisResult.reasons, messageBuilder);
		return messageBuilder.toString();
	}

}
