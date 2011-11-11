/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector.unittesting.matchers;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mutabilitydetector.AnalysisResult.analysisResult;
import static org.mutabilitydetector.IAnalysisSession.IsImmutable.IMMUTABLE;
import static org.mutabilitydetector.MutabilityReason.NULL_REASON;

import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.CheckerReasonDetail;

public class NoReasonsAllowedMatcherTest {

    @Test
    public void matchesWhenNoReasonsArePresent() throws Exception {
        AnalysisResult result = AnalysisResult.definitelyImmutable("a.b.c");
        assertThat(new NoReasonAllowedMatcher().matches(result), is(true));
    }

    @Test
    public void doesNotMatchWhenReasonsAreGiven() throws Exception {
        CheckerReasonDetail reason = new CheckerReasonDetail("message", null, NULL_REASON);
        AnalysisResult result = analysisResult("a.b.c", IMMUTABLE, reason);

        assertThat(new NoReasonAllowedMatcher().matches(result), is(false));
    }

}
