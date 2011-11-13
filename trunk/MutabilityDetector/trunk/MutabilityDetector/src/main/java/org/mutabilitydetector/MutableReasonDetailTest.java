package org.mutabilitydetector;

import org.junit.Test;

public class MutableReasonDetailTest {

    @Test(expected=NullPointerException.class)
    public void doesNotPermitNullMessage() throws Exception {
        MutableReasonDetail.newMutableReasonDetail(null, TestUtil.unusedCodeLocation(), MutabilityReason.NULL_REASON);
    }
    
    @Test(expected=NullPointerException.class)
    public void doesNotPermitNullCodeLocation() throws Exception {
        MutableReasonDetail.newMutableReasonDetail("unused message", null, MutabilityReason.NULL_REASON);
    }

    @Test(expected=NullPointerException.class)
    public void doesNotPermitNullReason() throws Exception {
        MutableReasonDetail.newMutableReasonDetail("unused message", TestUtil.unusedCodeLocation(), null);
    }
    
}
