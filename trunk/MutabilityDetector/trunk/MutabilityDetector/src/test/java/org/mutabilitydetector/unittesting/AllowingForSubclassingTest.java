package org.mutabilitydetector.unittesting;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mutabilitydetector.IAnalysisSession.IsImmutable.NOT_IMMUTABLE;
import static org.mutabilitydetector.MutabilityReason.ARRAY_TYPE_INHERENTLY_MUTABLE;
import static org.mutabilitydetector.MutabilityReason.ESCAPED_THIS_REFERENCE;
import static org.mutabilitydetector.MutabilityReason.FIELD_CAN_BE_REASSIGNED;
import static org.mutabilitydetector.MutabilityReason.NOT_DECLARED_FINAL;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.CheckerReasonDetail;
import org.mutabilitydetector.locations.CodeLocation;

public class AllowingForSubclassingTest {

    private final CodeLocation<?> unusedCodeLocation = null;
    private final String unusedClassName = null;

    @Test
    public void matchesAnalysisResultWithOnlyReasonBeingNonFinalClass() throws Exception {
        AllowingForSubclassing matcher = new AllowingForSubclassing();
        CheckerReasonDetail reason = new CheckerReasonDetail(unusedClassName, unusedCodeLocation, NOT_DECLARED_FINAL);
        AnalysisResult result = new AnalysisResult("org.some.NonFinalClass", NOT_IMMUTABLE, Arrays.asList(reason));

        assertTrue(matcher.matches(result));
    }

    @Test
    public void doesNotMatchWhenOnlyReasonIsSomethingOtherThanNotBeingFinal() throws Exception {
        AllowingForSubclassing matcher = new AllowingForSubclassing();
        CheckerReasonDetail reason = new CheckerReasonDetail(unusedClassName,
                unusedCodeLocation,
                FIELD_CAN_BE_REASSIGNED);
        AnalysisResult result = new AnalysisResult(unusedClassName, NOT_IMMUTABLE, Arrays.asList(reason));

        assertFalse(matcher.matches(result));
    }

    @Test
    public void matchesWhenNotBeingFinalIsOneOfSeveralReasons() throws Exception {
        AllowingForSubclassing matcher = new AllowingForSubclassing();
        Collection<CheckerReasonDetail> reasons = asList(
                new CheckerReasonDetail(unusedClassName, unusedCodeLocation, FIELD_CAN_BE_REASSIGNED),
                new CheckerReasonDetail(unusedClassName, unusedCodeLocation, NOT_DECLARED_FINAL), 
                new CheckerReasonDetail(unusedClassName, unusedCodeLocation, ARRAY_TYPE_INHERENTLY_MUTABLE));

        AnalysisResult result = new AnalysisResult(unusedClassName, NOT_IMMUTABLE, reasons);

        assertTrue(matcher.matches(result));
    }
    
    @Test
    public void doesNotMatchWhenNoneOfSeveralReasonsIsNotBeingFinal() throws Exception {
        AllowingForSubclassing matcher = new AllowingForSubclassing();
        Collection<CheckerReasonDetail> reasons = asList(
                new CheckerReasonDetail(unusedClassName, unusedCodeLocation, FIELD_CAN_BE_REASSIGNED),
                new CheckerReasonDetail(unusedClassName, unusedCodeLocation, ESCAPED_THIS_REFERENCE), 
                new CheckerReasonDetail(unusedClassName, unusedCodeLocation, ARRAY_TYPE_INHERENTLY_MUTABLE));

        AnalysisResult result = new AnalysisResult(unusedClassName, NOT_IMMUTABLE, reasons);

        assertFalse(matcher.matches(result));
    }

}
