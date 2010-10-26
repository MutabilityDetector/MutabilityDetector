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
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.mutabilitydetector.IAnalysisSession.AnalysisResult;
import org.mutabilitydetector.IAnalysisSession.IsImmutable;

public class IsImmutableMatcher extends TypeSafeDiagnosingMatcher<AnalysisResult> {
	private final IsImmutable isImmutable;
	public IsImmutableMatcher(IsImmutable isImmutable) {
		this.isImmutable = isImmutable;
	}
	@Override public boolean matchesSafely(AnalysisResult item, Description mismatchDescription) {
		mismatchDescription.appendDescriptionOf(this);
		return this.isImmutable == item.isImmutable;
	}
	@Override public void describeTo(Description description) {
		description.appendText(isImmutable + " immutable");
	}
	
	
}