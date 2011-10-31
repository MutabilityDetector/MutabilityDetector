package org.mutabilitydetector.checkers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.mutabilitydetector.CheckerReasonDetail;
import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.locations.ClassLocation;

public class AbstractMutabilityCheckerTest {

    private final IMutabilityChecker defaultChecker = new AbstractMutabilityChecker() {
    };

    @Test
    public void convertsTheResultOfAnalysisToCouldNotAnalyseWhenAskedToVisitAnAnalysisException() {
        defaultChecker.visit(0, 0, "some/class/ToAnalyse.class", null, null, null);
        defaultChecker.visitAnalysisException(new RuntimeException());

        assertThat(defaultChecker.result(), is(MutabilityReason.CANNOT_ANALYSE.createsResult()));
    }

    @Test
    public void providesADescriptiveReasonAfterVisitingAnAnalysisException() {
        defaultChecker.visit(0, 0, "some/class/ToAnalyse.class", null, null, null);
        defaultChecker.visitAnalysisException(new RuntimeException());
        CheckerReasonDetail reason = new CheckerReasonDetail("Encountered an unhandled error in analysis.",
                ClassLocation.fromInternalName("some/class/ToAnalyse.class"),
                MutabilityReason.CANNOT_ANALYSE);
        assertThat(defaultChecker.reasons(), contains(is(Matchers.equalTo(reason))));
    }
}
