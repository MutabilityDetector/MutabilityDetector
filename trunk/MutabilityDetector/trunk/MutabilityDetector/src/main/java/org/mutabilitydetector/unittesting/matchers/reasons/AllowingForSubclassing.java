package org.mutabilitydetector.unittesting.matchers.reasons;

import static org.mutabilitydetector.MutabilityReason.ABSTRACT_TYPE_INHERENTLY_MUTABLE;
import static org.mutabilitydetector.MutabilityReason.NOT_DECLARED_FINAL;

import org.hamcrest.Description;
import org.mutabilitydetector.MutableReasonDetail;

public class AllowingForSubclassing extends BaseMutableReasonDetailMatcher {

    @Override
    public void describeTo(Description description) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    protected boolean matchesSafely(MutableReasonDetail checkerReasonDetail, Description mismatchDescription) {
        return checkerReasonDetail.reason().isOneOf(NOT_DECLARED_FINAL, ABSTRACT_TYPE_INHERENTLY_MUTABLE);
    }

}

