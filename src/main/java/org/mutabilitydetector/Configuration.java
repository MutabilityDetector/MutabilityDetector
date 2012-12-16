package org.mutabilitydetector;

import java.util.Map;

import org.mutabilitydetector.CheckerRunner.ExceptionPolicy;
import org.mutabilitydetector.locations.Dotted;
import org.mutabilitydetector.unittesting.MutabilityAsserter;

/**
 * Allows customisation of Mutability Detector's analysis.
 * <p>
 * The most significant feature of {@link Configuration} is to allow defining
 * hardcoded results for particular classes, which should be respected during
 * analysis.
 * <p>
 * 
 * @see ConfigurationBuilder
 * @see MutabilityAsserter#configured(Configuration)
 * @see MutabilityAsserter#configured(ConfigurationBuilder)
 * @see ThreadUnsafeAnalysisSession#createWithCurrentClassPath(Configuration)
 */
public interface Configuration {

    /**
     * Add a predefined result used during analysis.
     * <p>
     * Hardcoding a result means that information queried about a class will
     * honour the result you have set. For example, if during analysis,
     * Mutability Detector has to discover whether a field type is mutable or
     * not. However, requesting the {@link AnalysisResult} of the class in
     * question directly will return the real result from the actual analysis.
     * This holds for unit tests, command line runs, and runtime analysis. As
     * such, calling this method will have no effect when querying an
     * AnalysisResult directly.
     * 
     * @see AnalysisResult
     * @see AnalysisSession#resultFor(org.mutabilitydetector.locations.Dotted)
     */
    Map<Dotted, AnalysisResult> hardcodedResults();

    /**
     * Configures how Mutability Detector's analysis should respond to
     * exceptions during analysis.
     * <p>
     * During analysis, an exception may occur which is recoverable. That is,
     * Mutability Detector is able to continue it's analysis, and <b>may</b>
     * produce valid results.
     * <p>
     * Setting this configuration flag to {@link ExceptionPolicy#FAIL_FAST},
     * will cause any unhandled exceptions will propagate, causing a failing
     * test or aborting a command line run.
     * <p>
     * Setting this configuration flag to {@link ExceptionPolicy#CARRY_ON} may
     * allow analysis to function where exceptions don't necessarily preclude a
     * useful output. For example, consider a class which you wish to make
     * immutable; a test for that class fails with an unhandled exception. If
     * that test has, say 10 reasons for mutability, and 1 of those causes the
     * test to abort with an exception, you have just lost out on 90% of the
     * required information. {@link ExceptionPolicy#CARRY_ON} will allow the
     * test to report 9 out of 10 reasons. The test may be useful, although it
     * won't be comprehensive.
     * <p>
     * 
     * If you are unlucky enough to have a class which causes exceptions during
     * analysis, please report it to the Mutability Detector project, at the <a
     * href="https
     * ://github.com/MutabilityDetector/MutabilityDetector/issues">project
     * homepage</a>.
     * 
     * 
     * @param exceptionPolicy
     *            - how to respond to exceptions during analysis. Defaults to
     */
    ExceptionPolicy exceptionPolicy();
}