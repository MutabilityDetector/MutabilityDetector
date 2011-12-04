/*
 *    Copyright (c) 2008-2011 Graham Allan
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.mutabilitydetector.unittesting;

import static java.util.Arrays.asList;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;
import static org.mutabilitydetector.unittesting.matchers.reasons.WithAllowedReasonsMatcher.withAllowedReasons;
import static org.mutabilitydetector.unittesting.matchers.reasons.WithAllowedReasonsMatcher.withNoAllowedReasons;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matcher;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.unittesting.internal.AnalysisSessionHolder;
import org.mutabilitydetector.unittesting.internal.AssertionReporter;
import org.mutabilitydetector.unittesting.matchers.reasons.WithAllowedReasonsMatcher;

/**
 * 
 * <h1>Mutability Detector</h1>
 * <p>
 * <i>Mutability Detector allows you to write a unit test that checks your classes are immutable.</i>
 * </p>
 * <h1>Help Guide</h2>
 * <h2>Contents</h2>
 * <ol>
 * <li>Preamble
 * <ul>
 * <li><a href="#AboutHelpGuide">About this help guide</a></li>
 * <li><a href="#AboutExamples">About these examples</a></li>
 * </ul>
 * </li>
 * <li><a href="#FirstTestCase">Your first test case.</a></li>
 * <li><a href="#ConfiguringTheAssertion">A more specific assertion</a></li>
 * <li><a href="#AllowingAReason">Allowing a reason for mutability</a></li>
 * </ol>
 * 
 * 
 * 
 * 
 * <h4 id="AboutHelpGuide">About this help guide</h4>
 * <p>
 * The help contents here are also available on the project homepage, currently at <a
 * href=http://code.google.com/p/mutability-detector/>http://code.google.com/p/mutability-detector/</a>.
 * </p>
 * This style of documentation is used as it provides content suitable for a web page and for offline use in the JavaDoc
 * viewer of your favourite IDE. It has been <strike>shamelessly stolen from</strike> inspired by the Mockito project,
 * thanks guys.
 * 
 * <h4 id="AboutExamples">About these examples</h4> I am assuming JUnit as the unit testing library. However, Mutability
 * Detector should work with any unit testing library that uses the exception mechanism for their assertions, such as
 * TestNG. If Mutability Detector is incompatible with your favourite testing library, please get in touch, and we'll
 * see what we can do about that.
 * 
 * <h3 id="FirstTestCase">Your first test case.</h3>
 * 
 * The most simple assertion you can make will look something like this: 
 * <pre><code>
 * import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutable;
 * 
 * &#064;Test public void checkMyClassIsImmutable() {
 *     assertImmutable(MyClass.class); 
 * }
 * </code></pre>
 * <p>
 * This assertion will trigger an analysis of
 * <code>MyClass</code>, passing if found to be immutable, failing if found to be mutable.
 * </p>
 * 
 * 
 * <h3 id="ConfiguringTheAssertion">Configuring the assertion</h3> 
 * <p>
 * The method used above is a shortcut for more
 * expressive forms of the assertion, and does not allow any further configuration. An equivalent assertion is:
 * </p>
 * 
 * <pre><code>
 * import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
 * import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;
 * 
 * &#064;Test public void checkMyClassIsImmutable() {
 *     assertInstancesOf(MyClass.class, areImmutable()); 
 * }</code></pre> 
 * 
 * 
 * This is the form that can be used for extra configuration of the assertion. Let's take a look at an
 * assertion that is configured differently. Consider a class which is immutable, except for fields not being declared
 * <code>final</code>. According to <a href="http://jcip.net/">Java Concurrency In Practice</a>, instances of classes
 * like this, as long as they are <i>safely publised</i> are still considered <i>effectively immutable</i>. Please note
 * however, Mutability Detector does not check that objects are safely published. <br />
 * To represent this in a unit test, the assertion would like this:
 * 
 * <pre><code>
 * import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
 * import static org.mutabilitydetector.unittesting.MutabilityMatchers.areEffectivelyImmutable;
 * 
 * &#064;Test public void checkMyClassIsImmutable() {
 *     assertInstancesOf(MyClassWhereTheFieldsAreNotFinal.class, areEffectivelyImmutable());
 * }</code></pre>
 * 
 * See also:
 * <ul>
 * <li>{@link IsImmutable#EFFECTIVELY_IMMUTABLE}</li>
 * </ul>
 * 
 * <p>
 * The second parameter to the method {@link MutabilityAssert#assertInstancesOf(Class, Matcher)} is a
 * <code>Matcher&lt;AnalysisResult&gt;</code>, where <code>Matcher</code> is a <a
 * href="http://code.google.com/p/hamcrest/">hamcrest matcher</a>, and {@link AnalysisResult} is provided by Mutability
 * Detector to represent the result of the static analysis performed on the given class. This means, if none of the
 * out-of-the-box matchers are quite right for your scenario, you can supply your own. Your implementation of
 * {@link Matcher#matches(Object)} should return true for a test pass, false for a test failure.
 * </p>
 * 
 * 
 * <h3 id="AllowingAReason">Allowing a reason</h3>
 * There can also be cases where your class is found to be mutable, but you know for your scenario that it's an
 * acceptable reason. Consider the following class:
 * 
 * <pre><code>public abstract class AbstractIntHolder {
 *   private final int intField;
 *   
 *   public AbstractIntHolder(int intToStore) {
 *     this.intField = intToStore;
 *   }
 * }</code></pre>
 * 
 * <p>
 * In this case, if you assert <code>AbstractIntHolder</code> is immutable, the test will fail. This is because
 * AbstractIntHolder can be subclassed, which means clients of this class, who for example, accept parameters of this
 * type and store them to fields, cannot depend on receiving a concrete, immutable object. If, in your code, you know
 * that all subclasses will also be immutable (hopefully you have tests for them too) then you can say that it is okay
 * that this class can be subclassed, because you know all subclasses are immutable as well.
 * </p>
 * <p>
 * Given such a scenario, the way to get your test to pass, and still provide a check that the class doesn't become
 * mutable by some other cause, is to <i>allow a reason</i> for mutability. An example of allowing said reason for
 * <code>AbstractIntHolder</code> could look like this:
 * </p>
 * 
 * <pre><code>
 * import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
 * import static org.mutabilitydetector.unittesting.MutabilityMatchers.areEffectivelyImmutable;
 * import static org.mutabilitydetector.unittesting.AllowedReason.allowingForSubclassing;
 * 
 * &#064;Test public void checkMyClassIsImmutable() {
 *     assertInstancesOf(AbstractIntHolder.class, areImmutable(), allowingForSubclassing());
 * }
 * </code></pre>
 * 
 * This will allow your test to pass, but fail for any other reasons that are introduced, e.g. if someone adds a setter
 * method.
 * 
 * <p>
 * Similar to the <code>Matcher&ltAnalysisResult&gt;</code> parameter, the allowed reason parameter of
 * {@link #assertInstancesOf(Class, Matcher, Matcher)} is a <code>Matcher&lt;{@link MutableReasonDetail}&gt;</code>. Mutability
 * Detector will provide only a few out-of-the-box implementations for this, which are unlikely to cover each scenario
 * where you want to permit a certain aspect of mutability.
 * </p>
 * <p>
 * For a mutable class to pass the test, each {@link MutableReasonDetail} must be matched by at least one allowed reason.
 * </p>
 * 
 * @author Graham Allan / Grundlefleck at gmail dot com
 * 
 * @see MutabilityMatchers
 * @see AllowedReason
 * @see AnalysisResult
 * @see MutableReasonDetail
 * @see IsImmutable
 * 
 */
public final class MutabilityAssert {
    
    private MutabilityAssert() { }

    private final static AssertionReporter reporter = new AssertionReporter();

    public static void assertImmutable(Class<?> expectedImmutableClass) {
        reporter.assertThat(getResultFor(expectedImmutableClass), withNoAllowedReasons(areImmutable()));
    }

    
    public static void assertInstancesOf(Class<?> clazz, Matcher<AnalysisResult> mutabilityMatcher) {
        reporter.assertThat(getResultFor(clazz), withNoAllowedReasons(mutabilityMatcher));
    }

    @SuppressWarnings("unchecked")
    public static void assertInstancesOf(Class<?> clazz,
            Matcher<AnalysisResult> mutabilityMatcher,
            Matcher<MutableReasonDetail> allowing) {
        WithAllowedReasonsMatcher areImmutable_withReasons = withAllowedReasons(mutabilityMatcher, asList((allowing)));
        reporter.assertThat(getResultFor(clazz), areImmutable_withReasons);
    }

    @SuppressWarnings("unchecked")
    public static void assertInstancesOf(Class<?> clazz,
            Matcher<AnalysisResult> mutabilityMatcher,
            Matcher<MutableReasonDetail> allowingFirst,
            Matcher<MutableReasonDetail> allowingSecond) {

        WithAllowedReasonsMatcher areImmutable_withReasons = withAllowedReasons(mutabilityMatcher,
                                                                                asList(allowingFirst, allowingSecond));
        reporter.assertThat(getResultFor(clazz), areImmutable_withReasons);
    }

    @SuppressWarnings("unchecked")
    public static void assertInstancesOf(Class<?> clazz,
            Matcher<AnalysisResult> mutabilityMatcher,
            Matcher<MutableReasonDetail> allowingFirst,
            Matcher<MutableReasonDetail> allowingSecond,
            Matcher<MutableReasonDetail> allowingThird) {

        WithAllowedReasonsMatcher areImmutable_withReasons = withAllowedReasons(mutabilityMatcher,
                                                                                asList(allowingFirst,
                                                                                       allowingSecond,
                                                                                       allowingThird));

        reporter.assertThat(getResultFor(clazz), areImmutable_withReasons);
    }

    public static void assertInstancesOf(Class<?> clazz,
            Matcher<AnalysisResult> mutabilityMatcher,
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

    private static AnalysisResult getResultFor(Class<?> clazz) {
        return AnalysisSessionHolder.analysisResultFor(clazz);
    }

}
