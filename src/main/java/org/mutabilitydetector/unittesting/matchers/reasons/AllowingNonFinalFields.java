package org.mutabilitydetector.unittesting.matchers.reasons;

import static org.mutabilitydetector.MutabilityReason.NON_FINAL_FIELD;

import org.mutabilitydetector.MutableReasonDetail;

public class AllowingNonFinalFields extends BaseMutableReasonDetailMatcher {

    @Override
    protected boolean matchesSafely(MutableReasonDetail reason) {
        return reason.reason().isOneOf(NON_FINAL_FIELD);
    }

}
