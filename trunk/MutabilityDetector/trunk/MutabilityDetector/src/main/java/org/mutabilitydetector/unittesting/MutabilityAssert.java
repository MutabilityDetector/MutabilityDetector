/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector.unittesting;

import static java.util.Arrays.asList;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;
import static org.mutabilitydetector.unittesting.matchers.IsImmutableMatcher.hasIsImmutableStatusOf;
import static org.mutabilitydetector.unittesting.matchers.reasons.WithAllowedReasonsMatcher.withAllowedReasons;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matcher;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.CheckerReasonDetail;
import org.mutabilitydetector.IAnalysisSession.IsImmutable;
import org.mutabilitydetector.unittesting.internal.AnalysisSessionHolder;
import org.mutabilitydetector.unittesting.internal.AssertionReporter;
import org.mutabilitydetector.unittesting.matchers.IsImmutableMatcher;
import org.mutabilitydetector.unittesting.matchers.reasons.WithAllowedReasonsMatcher;

public class MutabilityAssert {

    private final static AssertionReporter reporter = new AssertionReporter();

    public static void assertImmutable(Class<?> expectedImmutableClass) {
        reporter.assertThat(getResultFor(expectedImmutableClass), areImmutable());
    }

    public static void assertImmutableStatusIs(IsImmutable expected, Class<?> forClass) {
        reporter.assertThat(getResultFor(forClass), hasIsImmutableStatusOf(expected));
    }

    public static void assertInstancesOf(Class<?> clazz, IsImmutableMatcher areImmutable) {
        reporter.assertThat(getResultFor(clazz), areImmutable);
    }

    @SuppressWarnings("unchecked")
    public static void assertInstancesOf(Class<?> clazz, IsImmutableMatcher areImmutable, Matcher<CheckerReasonDetail> allowing) {
        WithAllowedReasonsMatcher areImmutable_withReasons = withAllowedReasons(areImmutable, asList((allowing)));
        reporter.assertThat(getResultFor(clazz), areImmutable_withReasons);
    }

    @SuppressWarnings("unchecked")
    public static void assertInstancesOf(Class<?> clazz, IsImmutableMatcher areImmutable, 
                                         Matcher<CheckerReasonDetail> allowingFirst,
                                         Matcher<CheckerReasonDetail> allowingSecond) {
        
        WithAllowedReasonsMatcher areImmutable_withReasons = withAllowedReasons(areImmutable, asList(allowingFirst, allowingSecond));
        reporter.assertThat(getResultFor(clazz), areImmutable_withReasons);
    }
    
    
    @SuppressWarnings("unchecked")
    public static void assertInstancesOf(Class<?> clazz, IsImmutableMatcher areImmutable, 
                                         Matcher<CheckerReasonDetail> allowingFirst,
                                         Matcher<CheckerReasonDetail> allowingSecond,
                                         Matcher<CheckerReasonDetail> allowingThird) {
        
        WithAllowedReasonsMatcher areImmutable_withReasons = withAllowedReasons(areImmutable, asList(allowingFirst, allowingSecond, allowingThird));
        
        reporter.assertThat(getResultFor(clazz), areImmutable_withReasons);
    }

    public static void assertInstancesOf(Class<?> clazz, IsImmutableMatcher areImmutable, 
                                         Matcher<CheckerReasonDetail> allowingFirst,
                                         Matcher<CheckerReasonDetail> allowingSecond,
                                         Matcher<CheckerReasonDetail> allowingThird,
                                         Matcher<CheckerReasonDetail>... allowingRest) {
        
        List<Matcher<CheckerReasonDetail>> allowedReasonMatchers = new ArrayList<Matcher<CheckerReasonDetail>>();
        allowedReasonMatchers.add(allowingFirst);
        allowedReasonMatchers.add(allowingSecond);
        allowedReasonMatchers.add(allowingThird);
        allowedReasonMatchers.addAll(asList(allowingRest));
        
        WithAllowedReasonsMatcher areImmutable_withReasons = withAllowedReasons(areImmutable, allowedReasonMatchers);
        reporter.assertThat(getResultFor(clazz), areImmutable_withReasons);
    }
    
    private static AnalysisResult getResultFor(Class<?> clazz) {
        return AnalysisSessionHolder.analysisResultFor(clazz);
    }

}
