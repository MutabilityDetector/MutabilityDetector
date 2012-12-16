package org.mutabilitydetector;

import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Sets.newHashSet;
import static org.mutabilitydetector.locations.ClassNameConverter.CONVERTER;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

import org.mutabilitydetector.CheckerRunner.ExceptionPolicy;
import org.mutabilitydetector.locations.ClassNameConverter;
import org.mutabilitydetector.unittesting.MutabilityAssert;
import org.mutabilitydetector.unittesting.MutabilityAsserter;

import com.google.common.base.Equivalence;
import com.google.common.base.Equivalence.Wrapper;
import com.google.common.base.Equivalences;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Allows configuring a custom {@link MutabilityAsserter} for unit testing.
 * 
 * The most significant feature of {@link ConfigurationBuilder} is to allow
 * defining hardcoded results for particular classes, which should be respected
 * during analysis. For more details, see {@link MutabilityAssert}.
 * <p>
 * 
 * Users should subclass {@link ConfigurationBuilder} and override the
 * {@link #configure()} method, which should then be used to construct a
 * MutabilityAsserter instance.
 * 
 * For example:
 * 
 * <pre>
 * <code>
 * MutabilityAsserter myAsserter = MutabilityAsserter.configured(new ConfigurationBuilder() {
 *   &#064;Override public void configure() {
 *     
 *     hardcodeAsDefinitelyImmutable(SomeClass.class);
 *     setExceptionPolicy(ExceptionPolicy.CARRY_ON);
 *     
 *     mergeHardcodedResultsFrom(ConfigurationBuilder.JDK);
 *   }
 * });
 * </code>
 * </pre>
 * 
 * This class also provides an out-of-the-box configuration with hardcoded
 * results for common JDK classes. This includes classes where Mutability
 * Detector's analysis is incorrect, for example, java.lang.String,
 * java.lang.Integer (and other primitive wrapper types) and
 * java.math.BigDecimal.
 * 
 * @see ConfigurationBuilder#JDK
 * 
 */
@NotThreadSafe
public abstract class ConfigurationBuilder {
    
    /**
     * Non-exhaustive list of immutable classes from the standard JDK.
     * 
     * @see String
     * @see Boolean
     * @see Byte
     * @see Character
     * @see Short
     * @see Integer
     * @see Long
     * @see Float
     * @see Double
     * @see Class
     * @see BigDecimal
     * @see BigInteger
     */
    public static final Configuration JDK = new ConfigurationBuilder() {
        @Override
        public void configure() {
            hardcodeAsDefinitelyImmutable(String.class);
            hardcodeAsDefinitelyImmutable(Boolean.class);
            hardcodeAsDefinitelyImmutable(Byte.class);
            hardcodeAsDefinitelyImmutable(Character.class);
            hardcodeAsDefinitelyImmutable(Short.class);
            hardcodeAsDefinitelyImmutable(Integer.class);
            hardcodeAsDefinitelyImmutable(Long.class);
            hardcodeAsDefinitelyImmutable(Float.class);
            hardcodeAsDefinitelyImmutable(Double.class);
            hardcodeAsDefinitelyImmutable(Class.class);
            hardcodeAsDefinitelyImmutable(BigDecimal.class);
            hardcodeAsDefinitelyImmutable(BigInteger.class);
        }
    }.build();
    
    /**
     * Configurations with default settings and no hardcoded results.
     */
    public static final Configuration NO_CONFIGURATION = new ConfigurationBuilder() {
        @Override public void configure() { }
    }.build();

    private static final ExceptionPolicy DEFAULT_EXCEPTION_POLICY = ExceptionPolicy.FAIL_FAST;


    
    /**
     * Subclasses should override this method to configure mutability assertions.
     * 
     * The available configuration methods are listed below.
     * 
     * @see #hardcodeResult(AnalysisResult)
     * @see #hardcodeResults(AnalysisResult...)
     * @see #hardcodeResults(Iterable)
     * @see #hardcodeAsDefinitelyImmutable(Class)
     * @see #hardcodeAsDefinitelyImmutable(String)
     * 
     * @see #mergeHardcodedResultsFrom(Configuration)
     * 
     * @see #setExceptionPolicy(ExceptionPolicy)
     */
    public abstract void configure();
    
    public final Configuration build() {
        configure();
        return new DefaultConfiguration(hardcodedResults.build(), exceptionPolicy);
    }
    
    private ImmutableSet.Builder<AnalysisResult> hardcodedResults = ImmutableSet.builder();
    private ExceptionPolicy exceptionPolicy = DEFAULT_EXCEPTION_POLICY;
    
    
    /**
     * Add a predefined result used during analysis.
     *
     * @see AnalysisResult
     */
    protected final void hardcodeResult(AnalysisResult result) {
        hardcodedResults.add(result);
    }

    /**
     * Adds all the given AnalysisResults, as if hardcodeResult was called for
     * each element in the iterable.
     * 
     * @see AnalysisResult
     * @see #hardcodeResult(AnalysisResult)
     */
    protected final void hardcodeResults(Iterable<AnalysisResult> result) {
        hardcodedResults.addAll(result);
    }

    /**
     * Adds all the given AnalysisResults, as if hardcodeResult was called for
     * each element in the var args parameter.
     * 
     * @see AnalysisResult
     * @see #hardcodeResult(AnalysisResult)
     */
    protected final void hardcodeResults(AnalysisResult... result) {
        hardcodeResults(Arrays.asList(result));
    }
    
    /**
     * Hardcodes a result indicating the given class is Immutable.
     * 
     * @see AnalysisResult#definitelyImmutable(String)
     */
    protected final void hardcodeAsDefinitelyImmutable(Class<?> immutableClass) {
        hardcodeResult(AnalysisResult.definitelyImmutable(immutableClass.getName()));
    }
    

    /**
     * Hardcodes a result indicating the given class is Immutable.
     * 
     * The most reliable format of class name would be the dotted version of the
     * fully qualified name, e.g. java.lang.String. However, there will be a
     * "best effort" attempt to accept other formats, e.g. java/lang/String, or
     * java.lang.String.class.
     * 
     * @see ClassNameConverter
     * @see AnalysisResult#definitelyImmutable(String)
     */
    protected final void hardcodeAsDefinitelyImmutable(String immutableClassName) {
        hardcodeResult(AnalysisResult.definitelyImmutable(CONVERTER.dotted(immutableClassName)));
    }
    
    /**
     * Returns an immutable snapshot of the hardcoded results as at time of calling.
     * 
     * Note changes the returned Set will not allow modifications, and will not 
     * reflect changes to the underlying configuration.
     */
    protected final Set<AnalysisResult> getCurrentlyHardcodedResults() {
        return hardcodedResults.build();
    }

    /**
     * Merges the hardcoded results of this Configuration with the given
     * Configuration.
     * 
     * The resultant hardcoded results will be the union of the two sets of
     * hardcoded results. Where the AnalysisResult for a class is found in both
     * Configurations, the result from otherConfiguration will replace the
     * existing result in this Configuration. This replacement behaviour will
     * occur for subsequent calls to
     * {@link #mergeHardcodedResultsFrom(Configuration)}.
     * 
     * @param otherConfiguration - Configuration to merge hardcoded results with.
     */
    protected void mergeHardcodedResultsFrom(Configuration otherConfiguration) {
        Set<Wrapper<AnalysisResult>> union = 
                Sets.union(newHashSet(transform(hardcodedResults.build(), TO_CLASSNAME_EQUIVALENCE_WRAPPER)), 
                           copyOf(transform(otherConfiguration.hardcodedResults().values(), TO_CLASSNAME_EQUIVALENCE_WRAPPER)));
        
        Set<AnalysisResult> result = copyOf(transform(union, UNWRAP));
        
        hardcodedResults = ImmutableSet.<AnalysisResult>builder().addAll(result);
    }

    private static final Equivalence<AnalysisResult> CLASSNAME_EQUIVALENCE = Equivalences.equals().onResultOf(AnalysisResult.TO_CLASSNAME);

    private static final Function<AnalysisResult, Wrapper<AnalysisResult>> TO_CLASSNAME_EQUIVALENCE_WRAPPER = 
        new Function<AnalysisResult, Wrapper<AnalysisResult>>() {
            @Override public Wrapper<AnalysisResult> apply(AnalysisResult input) {
                return CLASSNAME_EQUIVALENCE.wrap(input);
            }
    };
    
    private Function<Wrapper<AnalysisResult>, AnalysisResult> UNWRAP = new Function<Wrapper<AnalysisResult>, AnalysisResult>() {
        @Override public AnalysisResult apply(Wrapper<AnalysisResult> input) { return input.get(); }
    };
    
    /**
     * Configures how Mutability Detector's analysis should respond to
     * exceptions during analysis.
     * <p>
     * During analysis, an exception may occur which is recoverable. That is,
     * Mutability Detector is able to continue it's analysis, and <b>may</b>
     * produce valid results.
     * <p>
     * The default behaviour is to use {@link ExceptionPolicy#FAIL_FAST},
     * meaning any unhandled exceptions will propogate up past the assertion,
     * and cause a failing test.
     * <p>
     * Setting this configuration flag to {@link ExceptionPolicy#CARRY_ON} may
     * allow unit tests to function where exceptions don't necessarily preclude
     * a useful output. For example, consider a class which you wish to make
     * immutable; a test for that class fails with an unhandled exception. If
     * that test has, say 10 reasons for mutability, and 1 of those causes the
     * test to abort with an exception, you have just lost out on 90% of the
     * required information. {@link ExceptionPolicy#CARRY_ON} will allow the
     * test to report 9 out of 10 reasons. The test may be useful, although it
     * won't be comprehensive.
     * <p>
     * 
     * If you are unlucky enough to have a test which results in an exception,
     * please report it to the Mutability Detector project, at the <a
     * href="https
     * ://github.com/MutabilityDetector/MutabilityDetector/issues">project
     * homepage</a>.
     * 
     * 
     * @param exceptionPolicy
     *            - how to respond to exceptions during analysis. Defaults to
     *            {@link ExceptionPolicy#FAIL_FAST}
     */
    protected final void setExceptionPolicy(ExceptionPolicy exceptionPolicy) {
        this.exceptionPolicy = exceptionPolicy;
    }
    

}
