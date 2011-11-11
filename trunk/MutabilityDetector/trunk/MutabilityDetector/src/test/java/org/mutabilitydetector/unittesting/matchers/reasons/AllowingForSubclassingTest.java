package org.mutabilitydetector.unittesting.matchers.reasons;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mutabilitydetector.MutabilityReason.ABSTRACT_TYPE_INHERENTLY_MUTABLE;
import static org.mutabilitydetector.MutabilityReason.FIELD_CAN_BE_REASSIGNED;
import static org.mutabilitydetector.MutabilityReason.NOT_DECLARED_FINAL;

import org.junit.Test;
import org.mutabilitydetector.CheckerReasonDetail;
import org.mutabilitydetector.locations.CodeLocation;
import org.mutabilitydetector.unittesting.matchers.reasons.AllowingForSubclassing;

public class AllowingForSubclassingTest {

    private final CodeLocation<?> unusedCodeLocation = null;
    private final String unusedClassName = null;

    @Test
    public void matchesCheckerReasonDetailWithReasonOfNotDeclaredFinal() throws Exception {
        AllowingForSubclassing matcher = new AllowingForSubclassing();
        assertTrue(matcher.matches(new CheckerReasonDetail(unusedClassName, unusedCodeLocation, NOT_DECLARED_FINAL)));
    }
    
    @Test
    public void matchesCheckerReasonDetailWithReasonOfBeingDeclaredAsAbstractType() throws Exception {
        AllowingForSubclassing matcher = new AllowingForSubclassing();
        assertTrue(matcher.matches(new CheckerReasonDetail(unusedClassName, unusedCodeLocation, ABSTRACT_TYPE_INHERENTLY_MUTABLE)));
    }

    @Test
    public void doesNotMatchWhenOnlyReasonIsSomethingUnrelatedToSubclassing() throws Exception {
        AllowingForSubclassing matcher = new AllowingForSubclassing();
        assertFalse(matcher.matches(new CheckerReasonDetail(unusedClassName, unusedCodeLocation, FIELD_CAN_BE_REASSIGNED)));
    }

}
