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

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mutabilitydetector.IAnalysisSession.IsImmutable.NOT_IMMUTABLE;
import static org.mutabilitydetector.MutabilityReason.ABSTRACT_TYPE_TO_FIELD;
import static org.mutabilitydetector.MutabilityReason.FIELD_CAN_BE_REASSIGNED;
import static org.mutabilitydetector.TestUtil.unusedCheckerReasonDetail;
import static org.mutabilitydetector.locations.ClassLocation.fromInternalName;
import static org.mutabilitydetector.unittesting.AllowedReason.noneAllowed;
import static org.mutabilitydetector.unittesting.AllowedReason.provided;

import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.CheckerReasonDetail;
import org.mutabilitydetector.benchmarks.types.InterfaceType;
import org.mutabilitydetector.unittesting.matchers.AnalysisResultMatcher;

public class AllowedReasonsTest {

    @Test
    public void providedClassIsAlsoImmutableAllowsAssigningAbstractType() throws Exception {
        CheckerReasonDetail reason = new CheckerReasonDetail("assigning abstract type [a.b.c]",
                fromInternalName("a/b/c"),
                ABSTRACT_TYPE_TO_FIELD);
        AnalysisResult result = new AnalysisResult("c.d.e", NOT_IMMUTABLE, asList(reason));

        AnalysisResultMatcher allowed = provided("a.b.c").isAlsoImmutable();
        assertThat(allowed.matches(result), is(true));
    }

    @Test
    public void allowedReasonsMatchWhenThereAreNoReasonsToBeginWith() throws Exception {
        AnalysisResult result = AnalysisResult.definitelyImmutable("a.b.c");
        AnalysisResultMatcher allowed = AllowedReason.provided("c.d.e").isAlsoImmutable();

        assertThat(allowed.matches(result), is(true));
    }

    @Test
    public void doesNotMatchWhenReasonIsNotAllowed() throws Exception {
        CheckerReasonDetail reason = new CheckerReasonDetail("has setter method",
                fromInternalName("a/b/c"),
                FIELD_CAN_BE_REASSIGNED);
        AnalysisResult result = new AnalysisResult("c.d.e", NOT_IMMUTABLE, reason);

        AnalysisResultMatcher allowed = AllowedReason.provided("a.b.c").isAlsoImmutable();
        assertThat(allowed.matches(result), is(false));
    }

    @Test
    public void providedMethodCanBeCalledWithClassObject() throws Exception {
        String message = format("assigning abstract type [%s] to field.", InterfaceType.class.getName());
        CheckerReasonDetail reason = new CheckerReasonDetail(message, fromInternalName("a/b/c"), ABSTRACT_TYPE_TO_FIELD);
        AnalysisResult result = new AnalysisResult(message, NOT_IMMUTABLE, reason);
        AnalysisResultMatcher allowed = AllowedReason.provided(InterfaceType.class).isAlsoImmutable();

        assertThat(allowed.matches(result), is(true));
    }

    @Test
    public void noneAllowedMatcherReturnsFalseForAnyReason() throws Exception {
        AnalysisResult result = new AnalysisResult("any", NOT_IMMUTABLE, unusedCheckerReasonDetail());
        AnalysisResultMatcher nonAllowed = noneAllowed();

        assertThat(nonAllowed.matches(result), is(false));
    }

    @Test
    public void noneAllowedMatcherReturnsTrueWhenNoReasons() throws Exception {
        AnalysisResult result = AnalysisResult.definitelyImmutable("a.b.c");
        assertThat(noneAllowed().matches(result), is(true));
    }

}
