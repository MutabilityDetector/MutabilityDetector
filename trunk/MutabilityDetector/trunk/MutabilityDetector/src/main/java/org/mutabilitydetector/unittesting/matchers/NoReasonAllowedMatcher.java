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

import org.hamcrest.Description;
import org.mutabilitydetector.AnalysisResult;

public class NoReasonAllowedMatcher extends BaseAnalysisResultMatcher {

	@Override protected boolean matchesSafely(AnalysisResult analysisResult, Description mismatchDescription) {
		return (analysisResult.reasons.size() == 0);
	}

	@Override public void describeTo(Description description) {
		throw new UnsupportedOperationException("Not yet implemented.");
	}

}
