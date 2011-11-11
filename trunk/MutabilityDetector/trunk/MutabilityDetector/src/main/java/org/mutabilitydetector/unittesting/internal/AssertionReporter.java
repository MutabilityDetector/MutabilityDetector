/*
 * Mutability Detector
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * 
 * Further licensing information for this project can be found in
 * license/LICENSE.txt
 */

package org.mutabilitydetector.unittesting.internal;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.unittesting.MutabilityAssertionError;

/**
 * {@link AssertionReporter} is responsible for making an assertion in a test fail, by preparing and throwing the
 * appropriate {@link MutabilityAssertionError}.
 */
public class AssertionReporter {


    public void assertThat(AnalysisResult analysisResult, Matcher<AnalysisResult> areImmutable) {
        try {
            MatcherAssert.assertThat(analysisResult, areImmutable);
        } catch (AssertionError e) {
            throw new MutabilityAssertionError(e.getMessage());
        }
    }
}
