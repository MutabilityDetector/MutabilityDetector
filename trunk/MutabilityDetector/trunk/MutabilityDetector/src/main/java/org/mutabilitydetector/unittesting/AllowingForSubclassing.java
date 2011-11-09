package org.mutabilitydetector.unittesting;

import static org.mutabilitydetector.MutabilityReason.NOT_DECLARED_FINAL;

import org.hamcrest.Description;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.CheckerReasonDetail;
import org.mutabilitydetector.unittesting.matchers.BaseAnalysisResultMatcher;

public class AllowingForSubclassing extends BaseAnalysisResultMatcher {

    @Override
    public void describeTo(Description description) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    protected boolean matchesSafely(AnalysisResult analysisResult, Description mismatchDescription) {
        for (CheckerReasonDetail reason : analysisResult.reasons) {
            if (reason.reason() == NOT_DECLARED_FINAL) {
                return true;
            }
        }
        return false;
        
        
    }

}

