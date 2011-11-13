/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector;

import java.util.ArrayList;

import org.junit.Test;

public class AnalysisResultTest {

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionWhenNotImmutableAndNoReasonsGiven_varargs() throws Exception {
        AnalysisResult.analysisResult("someclass", IsImmutable.NOT_IMMUTABLE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionWhenNotImmutableAndNoReasonsGiven_collection() throws Exception {
        AnalysisResult.analysisResult("someclass", IsImmutable.NOT_IMMUTABLE, new ArrayList<CheckerReasonDetail>());
    }

    @Test
    public void doesntThrowExceptionWhenImmutableAndNoReasonGiven() throws Exception {
        AnalysisResult.analysisResult("someclass", IsImmutable.IMMUTABLE);
    }

}
