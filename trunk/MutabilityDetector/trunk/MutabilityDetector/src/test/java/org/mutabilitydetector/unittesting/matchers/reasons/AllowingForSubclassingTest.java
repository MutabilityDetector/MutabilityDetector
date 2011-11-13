package org.mutabilitydetector.unittesting.matchers.reasons;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mutabilitydetector.MutabilityReason.ABSTRACT_TYPE_INHERENTLY_MUTABLE;
import static org.mutabilitydetector.MutabilityReason.FIELD_CAN_BE_REASSIGNED;
import static org.mutabilitydetector.MutabilityReason.NOT_DECLARED_FINAL;
import static org.mutabilitydetector.MutableReasonDetail.newMutableReasonDetail;

import org.junit.Test;
import org.mutabilitydetector.TestUtil;
import org.mutabilitydetector.locations.CodeLocation;

public class AllowingForSubclassingTest {

    private final CodeLocation<?> unusedCodeLocation = TestUtil.unusedCodeLocation();
    private final String unusedClassName = "";

    @Test
    public void matchesCheckerReasonDetailWithReasonOfNotDeclaredFinal() throws Exception {
        AllowingForSubclassing matcher = new AllowingForSubclassing();
        assertTrue(matcher.matches(newMutableReasonDetail(unusedClassName, unusedCodeLocation, NOT_DECLARED_FINAL)));
    }
    
    @Test
    public void matchesCheckerReasonDetailWithReasonOfBeingDeclaredAsAbstractType() throws Exception {
        AllowingForSubclassing matcher = new AllowingForSubclassing();
        assertTrue(matcher.matches(newMutableReasonDetail(unusedClassName, unusedCodeLocation, ABSTRACT_TYPE_INHERENTLY_MUTABLE)));
    }

    @Test
    public void doesNotMatchWhenOnlyReasonIsSomethingUnrelatedToSubclassing() throws Exception {
        AllowingForSubclassing matcher = new AllowingForSubclassing();
        assertFalse(matcher.matches(newMutableReasonDetail(unusedClassName, unusedCodeLocation, FIELD_CAN_BE_REASSIGNED)));
    }

}
