package org.mutabilitydetector;

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


import com.google.common.collect.ImmutableSetMultimap;
import org.mutabilitydetector.asmoverride.AsmVerifierFactory.ClassloadingOption;
import org.mutabilitydetector.checkers.CheckerRunner.ExceptionPolicy;
import org.mutabilitydetector.checkers.MutabilityCheckerFactory.ReassignedFieldAnalysisChoice;
import org.mutabilitydetector.checkers.info.CopyMethod;
import org.mutabilitydetector.config.HardcodedResultsUsage;
import org.mutabilitydetector.locations.Dotted;
import org.mutabilitydetector.unittesting.MutabilityAsserter;

import java.util.Map;
import java.util.Set;

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
 * @see DefaultCachingAnalysisSession#createWithCurrentClassPath(Configuration)
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
     * Decide how hardcoded results should be used.
     * <p>
     * The default is {@link HardcodedResultsUsage#LOOKUP_WHEN_REFERENCED}.
     *
     * @see HardcodedResultsUsage#LOOKUP_WHEN_REFERENCED
     * @see HardcodedResultsUsage#DIRECTLY_IN_ASSERTION
     */
    HardcodedResultsUsage howToUseHardcodedResults();

    /**
     *
     */
    Set<Dotted> immutableContainerClasses();

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
     * @return ExceptionPolicy
     *            - how to respond to exceptions during analysis. Defaults to
     */
    ExceptionPolicy exceptionPolicy();

    /**
     * Configures whether Mutability Detector loads classes or not during analysis.
     * <p>
     * Mutability Detector often needs to analyse classes other than the one
     * specified in order to gain a more accurate result. The default behaviour
     * is to load these classes from the current classpath. Setting this flag to
     * {@link ClassloadingOption#DISABLED} will instruct Mutability Detector not to
     * attempt to load classes, and instead use a method of analysing
     * all classes, which guarantees not to load classes. This can save on heap requirements
     * as Mutability Detector's non classloading approach requires less data than class loading.
     * <p>
     * For the moment, this option is recommended if you find classloading takes up too much heap,
     * and the classes loaded for analysis won't be loaded anyway.
     *
     * @return ClassloadingOption
     *          - whether to allow class loading for analysis or not
     */
    ClassloadingOption classloadingOption();

    /**
     * Only to be used in development. This method will never appear in a released version.
     */
    @Deprecated
    ReassignedFieldAnalysisChoice reassignedFieldAlgorithm();

    /**
     * Safe methods for copying collections when being assigned to a field in a class's constructor.
     * 
     * @return hardcodedCopyMethods - methods that are deemed safe for copying
     */
    ImmutableSetMultimap<String, CopyMethod> hardcodedCopyMethods();

}
