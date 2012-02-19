package org.mutabilitydetector.unittesting.matchers.reasons;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mutabilitydetector.MutabilityReason.ABSTRACT_TYPE_INHERENTLY_MUTABLE;
import static org.mutabilitydetector.MutabilityReason.NON_FINAL_FIELD;
import static org.mutabilitydetector.MutableReasonDetail.newMutableReasonDetail;
import static org.mutabilitydetector.TestUtil.unusedCodeLocation;

import org.junit.Test;
import org.mutabilitydetector.MutableReasonDetail;

public class AllowingNonFinalFieldsTest {

    @Test
    public void matchesAReasonOfHavingNonFinalField() throws Exception {
        MutableReasonDetail reason = newMutableReasonDetail("any message", unusedCodeLocation(), NON_FINAL_FIELD);
        
        assertThat(new AllowingNonFinalFields().matches(reason), is(true));
    }
    
    @Test
    public void doesNotMatchAnyOtherReason() throws Exception {
        MutableReasonDetail reason = newMutableReasonDetail("any message", unusedCodeLocation(), ABSTRACT_TYPE_INHERENTLY_MUTABLE);
        
        assertThat(new AllowingNonFinalFields().matches(reason), is(false));
    }
    
}
