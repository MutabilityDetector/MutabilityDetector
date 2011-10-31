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

import static org.mutabilitydetector.unittesting.ReasonsFormatter.formatReasons;

import org.hamcrest.Description;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.IAnalysisSession.IsImmutable;

public class IsImmutableMatcher extends BaseAnalysisResultMatcher {
    private final IsImmutable isImmutable;
    private AnalysisResult item;

    public IsImmutableMatcher(IsImmutable isImmutable) {
        this.isImmutable = isImmutable;
    }

    @Override
    public boolean matchesSafely(AnalysisResult item, Description mismatchDescription) {
        this.item = item;
        mismatchDescription.appendText(item.dottedClassName + " is " + item.isImmutable + " immutable");
        mismatchDescription.appendText(formatReasons(item.reasons));
        return this.isImmutable == item.isImmutable;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(item.dottedClassName + " which is " + isImmutable + " immutable");
    }

}
