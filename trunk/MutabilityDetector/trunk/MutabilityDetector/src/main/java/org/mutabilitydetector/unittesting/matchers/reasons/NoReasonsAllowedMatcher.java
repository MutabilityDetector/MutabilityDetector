/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector.unittesting.matchers.reasons;

import static org.hamcrest.core.IsNot.not;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsAnything;
import org.mutabilitydetector.CheckerReasonDetail;

public class NoReasonsAllowedMatcher extends BaseCheckerReasonDetailMatcher {
    private final Matcher<CheckerReasonDetail> nothingEver = not(IsAnything.<CheckerReasonDetail> anything());

    public static Matcher<CheckerReasonDetail> noWarningsAllowed() {
        return new NoReasonsAllowedMatcher();
    }

    @Override
    public void describeTo(Description description) {
        nothingEver.describeTo(description);
    }

    @Override
    protected boolean matchesSafely(CheckerReasonDetail item, Description mismatchDescription) {
        return nothingEver.matches(item);
    }
}
