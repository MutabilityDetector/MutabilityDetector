/**
 * 
 */
package de.htwg_konstanz.jia.mdclient;

import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collection;

import org.hamcrest.Matcher;
import org.mutabilitydetector.AnalysisResult;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 22.11.2012
 */
public final class AnalysisResultAsserter {

    private final Collection<ParentAwareMutableReasonDetail> reasons;

    public AnalysisResultAsserter(final AnalysisResult analysisResult) {
        this.reasons = DefaultParentAwareMutableReasonDetail.getInstancesFor(analysisResult);
    }

    public AnalysisResultAsserter andOneReasonIsThat(final Matcher<ParentAwareMutableReasonDetail> reasonMatcher) {
        assertThat(reasons, hasItem(reasonMatcher));
        return this;
    }

    public AnalysisResultAsserter andNoReasonIsThat(final Matcher<ParentAwareMutableReasonDetail> reasonMatcher) {
        assertThat(reasons, not(hasItem(reasonMatcher)));
        return this;
    }

    public AnalysisResultAsserter andTheReasonIsThat(final Matcher<ParentAwareMutableReasonDetail> reasonMatcher) {
        assertThat(reasons, everyItem(reasonMatcher));
        return this;
    }

}
