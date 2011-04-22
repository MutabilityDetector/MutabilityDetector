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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mutabilitydetector.AnalysisResult.definitelyImmutable;
import static org.mutabilitydetector.IAnalysisSession.IsImmutable.DEFINITELY;
import static org.mutabilitydetector.IAnalysisSession.IsImmutable.DEFINITELY_NOT;
import static org.mutabilitydetector.TestUtil.unusedCheckerReasonDetails;
import static org.mutabilitydetector.unittesting.AllowedReason.noneAllowed;
import static org.mutabilitydetector.unittesting.matchers.WithAllowedReasonsMatcher.withAllowedReasons;

import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;

public class WithAllowedReasonsMatcherTest {

	@Test public void failsWhenPrimaryResultFailsAndNoReasonsAreAllowed() throws Exception {
		IsImmutableMatcher isImmutable = new IsImmutableMatcher(DEFINITELY);
		AnalysisResult analysisResult = new AnalysisResult("some class", DEFINITELY_NOT, unusedCheckerReasonDetails());
		
		WithAllowedReasonsMatcher withReasonsMatcher = withAllowedReasons(isImmutable, noneAllowed());
		
		assertThat(withReasonsMatcher.matches(analysisResult), is(false));
	}
	
	@Test public void passesWhenPrimaryResultPasses() throws Exception {
		IsImmutableMatcher isImmutable = new IsImmutableMatcher(DEFINITELY);
		AnalysisResult analysisResult = definitelyImmutable("some.class");
		
		WithAllowedReasonsMatcher withReasonsMatcher = withAllowedReasons(isImmutable, noneAllowed());
		
		assertThat(withReasonsMatcher.matches(analysisResult), is(true));
	}
	
}
