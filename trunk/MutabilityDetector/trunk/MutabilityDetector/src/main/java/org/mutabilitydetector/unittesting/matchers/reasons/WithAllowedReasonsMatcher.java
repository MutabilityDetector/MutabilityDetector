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

import static java.lang.String.format;
import static java.util.Collections.singleton;
import static org.mutabilitydetector.unittesting.internal.ReasonsFormatter.formatReasons;
import static org.mutabilitydetector.unittesting.matchers.reasons.NoReasonsAllowedMatcher.noReasonsAllowed;

import java.util.ArrayList;
import java.util.Collection;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.MutableReasonDetail;

public final class WithAllowedReasonsMatcher extends BaseMatcher<AnalysisResult> {
    
    public static WithAllowedReasonsMatcher withAllowedReasons(Matcher<AnalysisResult> areImmutable,
                                                               Iterable<Matcher<MutableReasonDetail>> allowing) {
        return new WithAllowedReasonsMatcher(areImmutable, allowing);
    }
    
    public static WithAllowedReasonsMatcher withNoAllowedReasons(Matcher<AnalysisResult> areImmutable) {
        return withAllowedReasons(areImmutable, singleton(noReasonsAllowed()));
    }
    
    private final Matcher<AnalysisResult> isImmutable;
    private final Iterable<Matcher<MutableReasonDetail>> allowedReasonMatchers;

    private WithAllowedReasonsMatcher(Matcher<AnalysisResult> isImmutable, Iterable<Matcher<MutableReasonDetail>> allowedReasons) {
        this.isImmutable = isImmutable;
        this.allowedReasonMatchers = allowedReasons;
    }

    
    @Override
    public boolean matches(Object item) {
        return matchesSafely(checkIsValidToMatchOn(item), new StringDescription());
    }
    
    private AnalysisResult checkIsValidToMatchOn(Object item) {
        if (item == null || !(item instanceof AnalysisResult)) {
            throw new IllegalArgumentException(
               "Trying to pass " + item + "where an " + AnalysisResult.class.getSimpleName() + "is required. " +
               "This is probably a programmer error, not your fault. Please file an issue.");
        } else {
            return (AnalysisResult) item;
        }
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        matchesSafely(checkIsValidToMatchOn(item), description);
    }
    
    private boolean matchesSafely(AnalysisResult analysisResult, Description mismatchDescription) {
        boolean matches = true;
        if (!isImmutable.matches(analysisResult)) {
            matches = false;
            describeMismatchHandlingHamcrest1_1Matcher(analysisResult, mismatchDescription);
        }
        
        if (mutabilityReasonsHaveBeenAllowed(analysisResult.reasons, mismatchDescription)) {
            matches = true;
        }

        return matches;
    }

    private void describeMismatchHandlingHamcrest1_1Matcher(AnalysisResult analysisResult, Description mismatchDescription) {
        try {
            isImmutable.describeMismatch(analysisResult, mismatchDescription);
        } catch (NoSuchMethodError e) {
            mismatchDescription.appendText(format("%s is actually %s%n", analysisResult.dottedClassName, analysisResult.isImmutable));
        }
    }
    
    private boolean mutabilityReasonsHaveBeenAllowed(Collection<MutableReasonDetail> reasons, Description mismatchDescription) {
        Collection<MutableReasonDetail> unmatchedReasons = new ArrayList<MutableReasonDetail>(reasons);
        Collection<MutableReasonDetail> allowedReasons = collectAllowedReasons(reasons);
        
        unmatchedReasons.removeAll(allowedReasons);
        
        boolean allReasonsAllowed = unmatchedReasons.isEmpty();
        
        if (!allReasonsAllowed) {
            describeMismatchedReasons(mismatchDescription, unmatchedReasons, allowedReasons);
        }
        
        return allReasonsAllowed;
    }

    private Collection<MutableReasonDetail> collectAllowedReasons(Collection<MutableReasonDetail> reasons) {
        Collection<MutableReasonDetail> allowedReasons = new ArrayList<MutableReasonDetail>();
        for (MutableReasonDetail reasonDetail: reasons) {
            for (Matcher<MutableReasonDetail> allowedReasonMatcher: allowedReasonMatchers) {
                if (allowedReasonMatcher.matches(reasonDetail)) {
                    allowedReasons.add(reasonDetail);
                }
            }
        }
        return allowedReasons;
    }

    private void describeMismatchedReasons(Description mismatchDescription, Collection<MutableReasonDetail> unmatchedReasons, Collection<MutableReasonDetail> allowedReasons) {
        mismatchDescription.appendText(format("    Reasons:%n"));
        mismatchDescription.appendText(formatReasons(unmatchedReasons));
        mismatchDescription.appendText(format("    Allowed reasons:%n"));
        mismatchDescription.appendText(allowedReasons.isEmpty() ? "        None." : formatReasons(allowedReasons));
    }

    @Override
    public void describeTo(Description description) {
        isImmutable.describeTo(description);
    }


}
