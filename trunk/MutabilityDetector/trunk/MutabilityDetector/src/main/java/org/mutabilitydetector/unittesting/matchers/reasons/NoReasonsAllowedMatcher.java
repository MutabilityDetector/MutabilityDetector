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
import org.mutabilitydetector.MutableReasonDetail;

public class NoReasonsAllowedMatcher extends BaseMutableReasonDetailMatcher {
    private final Matcher<MutableReasonDetail> nothingEver = not(IsAnything.<MutableReasonDetail> anything());

    public static Matcher<MutableReasonDetail> noWarningsAllowed() {
        return new NoReasonsAllowedMatcher();
    }

    @Override
    public void describeTo(Description description) {
        nothingEver.describeTo(description);
    }

    @Override
    protected boolean matchesSafely(MutableReasonDetail item, Description mismatchDescription) {
        return nothingEver.matches(item);
    }
}
