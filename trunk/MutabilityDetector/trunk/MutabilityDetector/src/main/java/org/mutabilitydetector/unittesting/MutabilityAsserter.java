package org.mutabilitydetector.unittesting;

import static java.util.Arrays.asList;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;
import static org.mutabilitydetector.unittesting.matchers.reasons.WithAllowedReasonsMatcher.withAllowedReasons;
import static org.mutabilitydetector.unittesting.matchers.reasons.WithAllowedReasonsMatcher.withNoAllowedReasons;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matcher;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.Configuration;
import org.mutabilitydetector.ConfigurationBuilder;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.ThreadUnsafeAnalysisSession;
import org.mutabilitydetector.locations.Dotted;
import org.mutabilitydetector.unittesting.internal.AssertionReporter;
import org.mutabilitydetector.unittesting.matchers.reasons.WithAllowedReasonsMatcher;

public class MutabilityAsserter {

    private final AssertionReporter reporter;
    private final AnalysisSession analysisSession;

    MutabilityAsserter(AssertionReporter reporter, AnalysisSession analysisSession) {
        this.reporter = reporter;
        this.analysisSession = analysisSession;
    }

    public static MutabilityAsserter configured(Configuration configuration) {
        return new MutabilityAsserter(new AssertionReporter(), 
                ThreadUnsafeAnalysisSession.createWithCurrentClassPath(configuration));
    }

    public static MutabilityAsserter configured(ConfigurationBuilder configuration) {
        return new MutabilityAsserter(new AssertionReporter(), 
                ThreadUnsafeAnalysisSession.createWithCurrentClassPath(configuration.build()));
    }
    

    public void assertImmutable(Class<?> expectedImmutableClass) {
        reporter.assertThat(getResultFor(expectedImmutableClass), withNoAllowedReasons(areImmutable()));
    }

    public void assertInstancesOf(Class<?> clazz, Matcher<AnalysisResult> mutabilityMatcher) {
        reporter.assertThat(getResultFor(clazz), withNoAllowedReasons(mutabilityMatcher));
    }

    @SuppressWarnings("unchecked")
    public void assertInstancesOf(Class<?> clazz, Matcher<AnalysisResult> mutabilityMatcher, Matcher<MutableReasonDetail> allowing) {
        WithAllowedReasonsMatcher areImmutable_withReasons = withAllowedReasons(mutabilityMatcher, asList((allowing)));
        reporter.assertThat(getResultFor(clazz), areImmutable_withReasons);
    }

    @SuppressWarnings("unchecked")
    public void assertInstancesOf(Class<?> clazz, Matcher<AnalysisResult> mutabilityMatcher, Matcher<MutableReasonDetail> allowingFirst, Matcher<MutableReasonDetail> allowingSecond) {
    
        WithAllowedReasonsMatcher areImmutable_withReasons = withAllowedReasons(mutabilityMatcher,
                                                                                asList(allowingFirst, allowingSecond));
        reporter.assertThat(getResultFor(clazz), areImmutable_withReasons);
    }

    @SuppressWarnings("unchecked")
    public void assertInstancesOf(Class<?> clazz, Matcher<AnalysisResult> mutabilityMatcher, Matcher<MutableReasonDetail> allowingFirst, Matcher<MutableReasonDetail> allowingSecond, Matcher<MutableReasonDetail> allowingThird) {
    
        WithAllowedReasonsMatcher areImmutable_withReasons = withAllowedReasons(mutabilityMatcher,
                                                                                asList(allowingFirst,
                                                                                       allowingSecond,
                                                                                       allowingThird));
    
        reporter.assertThat(getResultFor(clazz), areImmutable_withReasons);
    }

    public void assertInstancesOf(Class<?> clazz, Matcher<AnalysisResult> mutabilityMatcher, Matcher<MutableReasonDetail> allowingFirst, Matcher<MutableReasonDetail> allowingSecond, Matcher<MutableReasonDetail> allowingThird, Matcher<MutableReasonDetail>... allowingRest) {
    
        List<Matcher<MutableReasonDetail>> allowedReasonMatchers = new ArrayList<Matcher<MutableReasonDetail>>();
        allowedReasonMatchers.add(allowingFirst);
        allowedReasonMatchers.add(allowingSecond);
        allowedReasonMatchers.add(allowingThird);
        allowedReasonMatchers.addAll(asList(allowingRest));
    
        WithAllowedReasonsMatcher areImmutable_withReasons = withAllowedReasons(mutabilityMatcher, allowedReasonMatchers);
        reporter.assertThat(getResultFor(clazz), areImmutable_withReasons);
    }

    private AnalysisResult getResultFor(Class<?> clazz) {
        return analysisSession.resultFor(Dotted.fromClass(clazz));
    }

}