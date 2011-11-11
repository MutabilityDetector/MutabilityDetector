package org.mutabilitydetector.unittesting.matchers.reasons;

import static org.mutabilitydetector.MutabilityReason.ABSTRACT_TYPE_INHERENTLY_MUTABLE;
import static org.mutabilitydetector.MutabilityReason.NOT_DECLARED_FINAL;

import org.hamcrest.Description;
import org.mutabilitydetector.CheckerReasonDetail;

public class AllowingForSubclassing extends BaseCheckerReasonDetailMatcher {

    @Override
    public void describeTo(Description description) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    protected boolean matchesSafely(CheckerReasonDetail checkerReasonDetail, Description mismatchDescription) {
        return checkerReasonDetail.reason().isOneOf(NOT_DECLARED_FINAL, ABSTRACT_TYPE_INHERENTLY_MUTABLE);
    }

}

