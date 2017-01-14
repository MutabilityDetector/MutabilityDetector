package org.mutabilitydetector.unittesting;

/*
 * #%L
 * MutabilityDetector
 * %%
 * Copyright (C) 2008 - 2014 Graham Allan
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import com.google.classpath.ClassPath;
import com.google.classpath.ClassPathFactory;
import com.google.common.collect.Lists;
import org.hamcrest.Matcher;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.Configuration;
import org.mutabilitydetector.ConfigurationBuilder;
import org.mutabilitydetector.Configurations;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.asmoverride.AsmVerifierFactory;
import org.mutabilitydetector.asmoverride.AsmVerifierFactory.ClassloadingOption;
import org.mutabilitydetector.asmoverride.ClassLoadingVerifierFactory;
import org.mutabilitydetector.asmoverride.NonClassLoadingVerifierFactory;
import org.mutabilitydetector.checkers.ClassPathBasedCheckerRunnerFactory;
import org.mutabilitydetector.checkers.MutabilityCheckerFactory;
import org.mutabilitydetector.checkers.MutabilityCheckerFactory.ReassignedFieldAnalysisChoice;
import org.mutabilitydetector.classloading.CachingAnalysisClassLoader;
import org.mutabilitydetector.classloading.ClassForNameWrapper;
import org.mutabilitydetector.locations.Dotted;
import org.mutabilitydetector.unittesting.internal.AssertionReporter;
import org.mutabilitydetector.unittesting.matchers.reasons.WithAllowedReasonsMatcher;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.mutabilitydetector.DefaultCachingAnalysisSession.createWithGivenClassPath;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;
import static org.mutabilitydetector.unittesting.matchers.reasons.WithAllowedReasonsMatcher.withAllowedReasons;
import static org.mutabilitydetector.unittesting.matchers.reasons.WithAllowedReasonsMatcher.withNoAllowedReasons;

/**
 * Performs Mutability Detector's analysis and produces unit-test-friendly
 * {@link AssertionError} if the result is not as expected.
 * <p>
 * Instances of this class provide the methods accessed by
 * {@link MutabilityAssert}. More detailed documentation can be found there.
 */
public class MutabilityAsserter {

    private final AssertionReporter reporter;
    private final AnalysisSession analysisSession;

    private MutabilityAsserter(AssertionReporter reporter, AnalysisSession analysisSession) {
        this.reporter = reporter;
        this.analysisSession = analysisSession;
    }

    /**
     * Create a new asserter with an existing {@link Configuration}.
     * <p>
     * Example:
     * <pre><code>
     * MutabilityAsserter.configured(MyConfigurations.DEFAULT_CONFIGURATIONS);
     * </code></pre>
     * @see Configurations
     * @see Configurations#JDK_CONFIGURATION
     * @see Configurations#NO_CONFIGURATION
     * @see Configurations#OUT_OF_THE_BOX_CONFIGURATION
     */
    public static MutabilityAsserter configured(Configuration configuration) {
        ClassPath classpath = new ClassPathFactory().createFromJVM();

        AsmVerifierFactory verifierFactory = configuration.classloadingOption() == ClassloadingOption.ENABLED
            ? new ClassLoadingVerifierFactory(new CachingAnalysisClassLoader(new ClassForNameWrapper()))
            : new NonClassLoadingVerifierFactory(classpath);

        AnalysisSession analysisSession = createWithGivenClassPath(classpath,
            new ClassPathBasedCheckerRunnerFactory(classpath, configuration.exceptionPolicy()),
            new MutabilityCheckerFactory(ReassignedFieldAnalysisChoice.NAIVE_PUT_FIELD_ANALYSIS, configuration.immutableContainerClasses()),
            verifierFactory,
            configuration);

        return new MutabilityAsserter(new AssertionReporter(), analysisSession);
    }

    /**
     * Create a new asserter with a {@link Configuration} as built by the given
     * {@link ConfigurationBuilder}.
     * <p>
     * Use this method when you want to build a one-time Configuration inline..
     * <p>
     * Example:
     * 
     * <pre>
     * <code>
     *  MutabilityAsserter.configured(new ConfigurationBuilder() { 
     *   &#064;Override public void configure() {
     *     hardcodeAsDefinitelyImmutable(ActuallyImmutable.class); 
     *   }
     * });
     * </code>
     * </pre>
     */
    public static MutabilityAsserter configured(ConfigurationBuilder configuration) {
        return configured(configuration.build());
    }

    /**
     * @see MutabilityAssert#assertImmutable(Class)
     */
    public void assertImmutable(Class<?> expectedImmutableClass) {
        reporter.assertThat(getResultFor(expectedImmutableClass), withNoAllowedReasons(areImmutable()));
    }

    /**
     * @see MutabilityAssert#assertInstancesOf(Class, Matcher)
     */
    public void assertInstancesOf(Class<?> clazz, Matcher<AnalysisResult> mutabilityMatcher) {
        reporter.assertThat(getResultFor(clazz), withNoAllowedReasons(mutabilityMatcher));
    }

    /**
     * @see MutabilityAssert#assertInstancesOf(Class, Matcher, Matcher)
     */
    @SuppressWarnings("unchecked")
    public void assertInstancesOf(Class<?> clazz, Matcher<AnalysisResult> mutabilityMatcher, 
                                  Matcher<MutableReasonDetail> allowing) {
        WithAllowedReasonsMatcher areImmutable_withReasons = withAllowedReasons(mutabilityMatcher, asList((allowing)));
        reporter.assertThat(getResultFor(clazz), areImmutable_withReasons);
    }

    /**
     * @see MutabilityAssert#assertInstancesOf(Class, Matcher, Matcher, Matcher)
     */
    @SuppressWarnings("unchecked")
    public void assertInstancesOf(Class<?> clazz, Matcher<AnalysisResult> mutabilityMatcher, 
                                  Matcher<MutableReasonDetail> allowingFirst, 
                                  Matcher<MutableReasonDetail> allowingSecond) {
    
        WithAllowedReasonsMatcher areImmutable_withReasons = withAllowedReasons(mutabilityMatcher,
                                                                                asList(allowingFirst, allowingSecond));
        reporter.assertThat(getResultFor(clazz), areImmutable_withReasons);
    }

    /**
     * @see MutabilityAssert#assertInstancesOf(Class, Matcher, Matcher, Matcher, Matcher)
     */
    @SuppressWarnings("unchecked")
    public void assertInstancesOf(Class<?> clazz, Matcher<AnalysisResult> mutabilityMatcher, 
                                  Matcher<MutableReasonDetail> allowingFirst, 
                                  Matcher<MutableReasonDetail> allowingSecond, 
                                  Matcher<MutableReasonDetail> allowingThird) {
    
        WithAllowedReasonsMatcher areImmutable_withReasons = withAllowedReasons(mutabilityMatcher,
                                                                                asList(allowingFirst,
                                                                                       allowingSecond,
                                                                                       allowingThird));
    
        reporter.assertThat(getResultFor(clazz), areImmutable_withReasons);
    }

    /**
     * @see MutabilityAssert#assertInstancesOf(Class, Matcher, Matcher, Matcher, Matcher, Matcher...)
     */
    public void assertInstancesOf(Class<?> clazz, Matcher<AnalysisResult> mutabilityMatcher, 
                                  Matcher<MutableReasonDetail> allowingFirst, 
                                  Matcher<MutableReasonDetail> allowingSecond, 
                                  Matcher<MutableReasonDetail> allowingThird, 
                                  Matcher<MutableReasonDetail>... allowingRest) {
    
        List<Matcher<MutableReasonDetail>> allowedReasonMatchers = new ArrayList<Matcher<MutableReasonDetail>>();
        allowedReasonMatchers.add(allowingFirst);
        allowedReasonMatchers.add(allowingSecond);
        allowedReasonMatchers.add(allowingThird);
        allowedReasonMatchers.addAll(asList(allowingRest));
    
        WithAllowedReasonsMatcher areImmutable_withReasons = withAllowedReasons(mutabilityMatcher, allowedReasonMatchers);
        reporter.assertThat(getResultFor(clazz), areImmutable_withReasons);
    }

    /**
     * @see MutabilityAssert#assertInstancesOf(Class, Matcher, Iterable)
     */
    public void assertInstancesOf(Class<?> clazz,
                                  Matcher<AnalysisResult> mutabilityMatcher,
                                  Iterable<Matcher<MutableReasonDetail>> allowingAll) {
        Iterable<Matcher<MutableReasonDetail>> allowedReasonMatchers = Lists.newArrayList(allowingAll);
        
        WithAllowedReasonsMatcher areImmutable_withReasons = withAllowedReasons(mutabilityMatcher, allowedReasonMatchers);
        reporter.assertThat(getResultFor(clazz), areImmutable_withReasons);
    }

    private AnalysisResult getResultFor(Class<?> clazz) {
        return analysisSession.resultFor(Dotted.fromClass(clazz));
    }

}