/**
 * 
 */
package de.htwg_konstanz.jia.mdclient;

import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collection;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hamcrest.Matcher;
import org.mutabilitydetector.AnalysisResult;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 22.11.2012
 */
@Immutable
public final class DefaultAnalysisResultAsserter implements AnalysisResultAsserter {

    private final Collection<ParentAwareMutableReasonDetail> reasons;

    private DefaultAnalysisResultAsserter(final AnalysisResult analysisResult) {
        Validate.notNull(analysisResult);
        this.reasons = DefaultParentAwareMutableReasonDetail.getInstancesFor(analysisResult);
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param analysisResult
     *            result of a preceding analysis session of
     *            <em>Mutability Detector</em>. Must not be {@code null}.
     * @return an instance of this class which is based on
     *         {@code analysisResult}.
     */
    public static DefaultAnalysisResultAsserter getInstance(final AnalysisResult analysisResult) {
        return new DefaultAnalysisResultAsserter(analysisResult);
    }

    @Override
    public AnalysisResultAsserter andOneReasonIsThat(final Matcher<ParentAwareMutableReasonDetail> reasonMatcher) {
        Validate.notNull(reasonMatcher);
        assertThat(reasons, hasItem(reasonMatcher));
        return this;
    }

    @Override
    public AnalysisResultAsserter andNoReasonIsThat(final Matcher<ParentAwareMutableReasonDetail> reasonMatcher) {
        Validate.notNull(reasonMatcher);
        assertThat(reasons, not(hasItem(reasonMatcher)));
        return this;
    }

    @Override
    public AnalysisResultAsserter andTheReasonIsThat(final Matcher<ParentAwareMutableReasonDetail> reasonMatcher) {
        Validate.notNull(reasonMatcher);
        assertThat(reasons, everyItem(reasonMatcher));
        return this;
    }

    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("reasons", reasons);
        return builder.toString();
    }

}
