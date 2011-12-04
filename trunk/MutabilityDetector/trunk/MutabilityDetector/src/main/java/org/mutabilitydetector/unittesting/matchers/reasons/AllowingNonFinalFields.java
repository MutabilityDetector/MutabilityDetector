package org.mutabilitydetector.unittesting.matchers.reasons;

import org.hamcrest.Description;
import org.mutabilitydetector.MutableReasonDetail;

public class AllowingNonFinalFields extends BaseMutableReasonDetailMatcher {

    @Override
    public void describeTo(Description description) {
        throw new UnsupportedOperationException("auto generated method stub");
    }

    @Override
    protected boolean matchesSafely(MutableReasonDetail item, Description mismatchDescription) {
        throw new UnsupportedOperationException("auto generated method stub");
    }

}
