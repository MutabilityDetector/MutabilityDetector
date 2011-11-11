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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.mutabilitydetector.AnalysisResult.analysisResult;
import static org.mutabilitydetector.IAnalysisSession.IsImmutable.IMMUTABLE;
import static org.mutabilitydetector.IAnalysisSession.IsImmutable.NOT_IMMUTABLE;
import static org.mutabilitydetector.MutabilityReason.ABSTRACT_TYPE_INHERENTLY_MUTABLE;
import static org.mutabilitydetector.TestUtil.unusedCheckerReasonDetails;
import static org.mutabilitydetector.locations.ClassLocation.fromInternalName;

import java.util.ArrayList;
import java.util.Collection;

import org.hamcrest.StringDescription;
import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.CheckerReasonDetail;

public class IsImmutableMatcherTest {

    IsImmutableMatcher matcher = IsImmutableMatcher.hasIsImmutableStatusOf(IMMUTABLE);
    
    
    @Test
    public void matchesForSameIsImmutableResult() throws Exception {
        AnalysisResult result = AnalysisResult.definitelyImmutable("a.b.c");
        assertThat(matcher.matches(result), is(true));
    }

    @Test
    public void doesNotMatchForDifferentIsImmutableResult() throws Exception {
        AnalysisResult nonMatchingResult = analysisResult("c.d.e", NOT_IMMUTABLE, unusedCheckerReasonDetails());
        assertThat(matcher.matches(nonMatchingResult), is(false));
    }

    @Test
    public void hasDescriptiveErrorMessageForMismatch() throws Exception {
        Collection<CheckerReasonDetail> reasons = new ArrayList<CheckerReasonDetail>();
        reasons.add(new CheckerReasonDetail("mutable coz i sez so",
                fromInternalName("c/d/e"),
                ABSTRACT_TYPE_INHERENTLY_MUTABLE));
        AnalysisResult nonMatchingResult = analysisResult("c.d.e", NOT_IMMUTABLE, reasons);

        StringDescription description = new StringDescription();
        matcher.describeMismatch(nonMatchingResult, description);

        assertThat(description.toString(), containsString("mutable coz i sez so"));
        assertThat(description.toString(), containsString(NOT_IMMUTABLE.name()));
    }

}
