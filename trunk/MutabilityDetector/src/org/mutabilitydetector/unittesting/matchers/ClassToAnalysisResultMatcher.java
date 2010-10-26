/*
 * Mutability Detector
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * 
 * Further licensing information for this project can be found in
 * license/LICENSE.txt
 */

package org.mutabilitydetector.unittesting.matchers;

import org.hamcrest.Description;
import org.mutabilitydetector.IAnalysisSession.AnalysisResult;

public class ClassToAnalysisResultMatcher extends ConvertingTypeSafeMatcher<Class<?>, AnalysisResult> {

	@Override public AnalysisResult convertTo(Class<?> from) {
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	@Override public boolean matchesConverted(AnalysisResult item, Description mismatchDescription) {
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	@Override public void describeTo(Description description) {
		throw new UnsupportedOperationException("Not yet implemented.");
	}


}
