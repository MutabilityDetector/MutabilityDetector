/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector.unittesting;

import org.hamcrest.Matcher;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.CheckerReasonDetail;
import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.unittesting.matchers.IsImmutableMatcher;
import org.mutabilitydetector.unittesting.matchers.reasons.NoReasonsAllowedMatcher;

public class MutabilityMatchers {

    public static Matcher<CheckerReasonDetail> noWarningsAllowed() {
        return new NoReasonsAllowedMatcher();
    }

    public static Matcher<AnalysisResult> areImmutable() {
        return IsImmutableMatcher.hasIsImmutableStatusOf(IsImmutable.IMMUTABLE);
    }
    
    public static Matcher<AnalysisResult> areEffectivelyImmutable() {
        return IsImmutableMatcher.hasIsImmutableStatusOf(IsImmutable.EFFECTIVELY_IMMUTABLE);
    }

    public static Matcher<AnalysisResult> areNotImmutable() {
        return IsImmutableMatcher.hasIsImmutableStatusOf(IsImmutable.NOT_IMMUTABLE);
    }
}
