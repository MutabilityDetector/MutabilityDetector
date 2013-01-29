/*
 *    Copyright (c) 2008-2011 Graham Allan
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.mutabilitydetector.unittesting.matchers.reasons;

import static org.hamcrest.core.IsNot.not;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsAnything;
import org.mutabilitydetector.MutableReasonDetail;

public class NoReasonsAllowedMatcher extends BaseMutableReasonDetailMatcher {
    private final Matcher<MutableReasonDetail> nothingEver = not(IsAnything.<MutableReasonDetail> anything());

    /**
     * Prefer {@link #noReasonsAllowed()}
     * Will be removed in release following 0.8
     */
    @Deprecated
    public static Matcher<MutableReasonDetail> noWarningsAllowed() {
        return new NoReasonsAllowedMatcher();
    }

    public static Matcher<MutableReasonDetail> noReasonsAllowed() {
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
