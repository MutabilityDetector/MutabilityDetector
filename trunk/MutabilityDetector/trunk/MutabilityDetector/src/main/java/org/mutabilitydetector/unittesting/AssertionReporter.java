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
import static org.mutabilitydetector.IAnalysisSession.IsImmutable.IMMUTABLE;
import static org.mutabilitydetector.unittesting.matchers.MutabilityMatchers.noWarningsAllowed;

import java.util.Collection;

import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.CheckerReasonDetail;
import org.mutabilitydetector.IAnalysisSession.IsImmutable;
import org.mutabilitydetector.unittesting.matchers.AnalysisResultMatcher;

/**
 * {@link AssertionReporter} is responsible for making an assertion in a test fail, by preparing and throwing the
 * appropriate {@link MutabilityAssertionError}.
 */
public class AssertionReporter {

    public void expectedImmutable(AnalysisResult analysisResult) {
        expectedIsImmutable(IMMUTABLE, analysisResult, noWarningsAllowed());
    }

    public String formatReasons(Collection<CheckerReasonDetail> reasons) {
        return ReasonsFormatter.formatReasons(reasons, new StringBuilder());
    }

    public void expectedIsImmutable(IsImmutable expected, AnalysisResult analysisResult) {
        this.expectedIsImmutable(expected, analysisResult, noWarningsAllowed());
    }

    private String buildExpectedIsImmutableExceptionMessage(IsImmutable expected,
            AnalysisResult analysisResult,
            AnalysisResultMatcher allowed) {
        StringBuilder messageBuilder = new StringBuilder();
        String className = getSimpleClassName(analysisResult);
        messageBuilder.append(format("\nExpected class [%s] to be [%s] immutable," + "\nbut was [%s].%n",
                className,
                expected,
                analysisResult.isImmutable));
        ReasonsFormatter.formatReasons(analysisResult.reasons, messageBuilder);
        messageBuilder.append("\n\tSuppressed reasons:");
        // messageBuilder.append("\n\t\t" + )
        return messageBuilder.toString();
    }

    private String getSimpleClassName(AnalysisResult analysisResult) {
        String dottedClassName = analysisResult.dottedClassName;
        return dottedClassName.substring(dottedClassName.lastIndexOf('.') + 1, dottedClassName.length());
    }

    public void expectedIsImmutable(IsImmutable expected, AnalysisResult analysisResult, AnalysisResultMatcher allowed) {
        if (gotTheExpectedResult(expected, analysisResult) || mutabilityReasonsHaveBeenSuppressed(analysisResult,
                allowed)) {
            return;
        } else {
            reportAssertionError(expected, analysisResult, allowed);
        }
    }

    private void reportAssertionError(IsImmutable expected, AnalysisResult analysisResult, AnalysisResultMatcher allowed) {
        String message = buildExpectedIsImmutableExceptionMessage(expected, analysisResult, allowed);
        throw new MutabilityAssertionError(message);
    }

    private boolean mutabilityReasonsHaveBeenSuppressed(AnalysisResult analysisResult, AnalysisResultMatcher allowed) {
        return allowed.matches(analysisResult);
    }

    private boolean gotTheExpectedResult(IsImmutable expected, AnalysisResult analysisResult) {
        return expected == analysisResult.isImmutable;
    }

}
