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

import java.util.ArrayList;
import java.util.Collection;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.CheckerReasonDetail;
import org.mutabilitydetector.unittesting.matchers.IsImmutableMatcher;

public class WithAllowedReasonsMatcher extends TypeSafeDiagnosingMatcher<AnalysisResult> {
    
    public static WithAllowedReasonsMatcher withAllowedReasons(IsImmutableMatcher areImmutable,
                                                               Iterable<Matcher<CheckerReasonDetail>> allowing) {
        return new WithAllowedReasonsMatcher(areImmutable, allowing);
    }
    
    private final IsImmutableMatcher isImmutable;
    private final Iterable<Matcher<CheckerReasonDetail>> allowedReasonMatchers;

    public WithAllowedReasonsMatcher(IsImmutableMatcher isImmutable, Iterable<Matcher<CheckerReasonDetail>> allowedReasons) {
        this.isImmutable = isImmutable;
        this.allowedReasonMatchers = allowedReasons;

    }

    @Override
    protected boolean matchesSafely(AnalysisResult analysisResult, Description mismatchDescription) {
        if (isImmutable.matches(analysisResult)) {
            return true;
        } else if (mutabilityReasonsHaveBeenSuppressed(analysisResult.reasons)) {
            return true;
        } else {
            isImmutable.describeMismatch(analysisResult, mismatchDescription);
            return false;
        }
    }
    
    private boolean mutabilityReasonsHaveBeenSuppressed(Collection<CheckerReasonDetail> reasons) {
        Collection<CheckerReasonDetail> allowedReasons = new ArrayList<CheckerReasonDetail>();
        Collection<CheckerReasonDetail> actualReasons = new ArrayList<CheckerReasonDetail>(reasons);
        
        for (CheckerReasonDetail reasonDetail: reasons) {
            for (Matcher<CheckerReasonDetail> allowedReasonMatcher: allowedReasonMatchers) {
                if (allowedReasonMatcher.matches(reasonDetail)) {
                    allowedReasons.add(reasonDetail);
                }
            }
        }
        
        actualReasons.removeAll(allowedReasons);
        
        return actualReasons.isEmpty();
    }

    @Override
    public void describeTo(Description description) {
        isImmutable.describeTo(description);
    }



}
