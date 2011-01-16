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

public class WithAllowedReasonsMatcher extends BaseAnalysisResultMatcher {


	public static WithAllowedReasonsMatcher withAllowedReasons(IsImmutableMatcher isImmutable, 
															   AnalysisResultMatcher allowedReason) {
		return new WithAllowedReasonsMatcher(isImmutable, allowedReason);
	}

	private final IsImmutableMatcher isImmutable;
	private final AnalysisResultMatcher allowedReason;

	public WithAllowedReasonsMatcher(IsImmutableMatcher isImmutable, AnalysisResultMatcher allowedReason) {
		this.isImmutable = isImmutable;
		this.allowedReason = allowedReason;

	}


	@Override protected boolean matchesSafely(AnalysisResult analysisResult, Description mismatchDescription) {
		if(isImmutable.matches(analysisResult)) { 
			return true;
		} else if(allowedReason.matches(analysisResult)){
			return true;
		} else {
			isImmutable.describeMismatch(analysisResult, mismatchDescription);
			return false;
		}		
	}

	@Override public void describeTo(Description description) {
		isImmutable.describeTo(description);
	}

}
