package org.mutabilitydetector.unittesting.matchers.reasons;

import static org.mutabilitydetector.MutabilityReason.NON_FINAL_FIELD;

import org.hamcrest.Description;
import org.mutabilitydetector.MutableReasonDetail;

public class AllowingNonFinalFields extends BaseMutableReasonDetailMatcher {

    @Override
    public void describeTo(Description description) {
        throw new UnsupportedOperationException("auto generated method stub");
    }

    @Override
    protected boolean matchesSafely(MutableReasonDetail reason, Description mismatchDescription) {
        return reason.reason().isOneOf(NON_FINAL_FIELD);
    }

}
