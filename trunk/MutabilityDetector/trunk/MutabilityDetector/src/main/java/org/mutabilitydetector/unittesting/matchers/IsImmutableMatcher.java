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

import static org.mutabilitydetector.unittesting.internal.ReasonsFormatter.formatReasons;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.IAnalysisSession.IsImmutable;

public class IsImmutableMatcher extends TypeSafeDiagnosingMatcher<AnalysisResult> {
    private final IsImmutable isImmutable;
    private AnalysisResult result;

    private IsImmutableMatcher(IsImmutable isImmutable) {
        this.isImmutable = isImmutable;
    }
    
    public static IsImmutableMatcher hasIsImmutableStatusOf(IsImmutable isImmutable) {
        return new IsImmutableMatcher(isImmutable);
    }

    @Override
    public boolean matchesSafely(AnalysisResult item, Description mismatchDescription) {
        this.result = item;
        mismatchDescription.appendText(item.dottedClassName + " is actually " + item.isImmutable);
        mismatchDescription.appendText("\n");
        mismatchDescription.appendText(formatReasons(item.reasons));
        return this.isImmutable == item.isImmutable;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(result.dottedClassName + " to be " + isImmutable);
    }

}
