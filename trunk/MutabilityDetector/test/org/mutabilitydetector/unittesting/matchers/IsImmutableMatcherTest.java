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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mutabilitydetector.IAnalysisSession.IsImmutable.DEFINITELY;
import static org.mutabilitydetector.IAnalysisSession.IsImmutable.DEFINITELY_NOT;
import static org.mutabilitydetector.TestUtil.unusedCheckerReasonDetails;

import org.junit.Test;
import org.mutabilitydetector.IAnalysisSession.AnalysisResult;

public class IsImmutableMatcherTest {

	@Test public void matchesForSameIsImmutableResult() throws Exception {
		IsImmutableMatcher matcher = new IsImmutableMatcher(DEFINITELY);
		AnalysisResult result = AnalysisResult.definitelyImmutable("a.b.c");
		assertThat(matcher.matches(result), is(true));
	}
	
	@Test public void doesNotMatchForDifferentIsImmutableResult() throws Exception {
		IsImmutableMatcher matcher = new IsImmutableMatcher(DEFINITELY);
		AnalysisResult nonMatchingResult = new AnalysisResult("c.d.e", DEFINITELY_NOT, unusedCheckerReasonDetails());
		assertThat(matcher.matches(nonMatchingResult), is(false));
	}
	
}
