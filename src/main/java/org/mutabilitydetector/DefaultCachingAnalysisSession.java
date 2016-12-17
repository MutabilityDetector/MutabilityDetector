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


import com.google.classpath.ClassPath;
import com.google.classpath.ClassPathFactory;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import org.mutabilitydetector.asmoverride.AsmVerifierFactory;
import org.mutabilitydetector.asmoverride.ClassLoadingVerifierFactory;
import org.mutabilitydetector.checkers.AllChecksRunner;
import org.mutabilitydetector.checkers.CheckerRunnerFactory;
import org.mutabilitydetector.checkers.ClassPathBasedCheckerRunnerFactory;
import org.mutabilitydetector.checkers.MutabilityCheckerFactory;
import org.mutabilitydetector.checkers.info.AnalysisDatabase;
import org.mutabilitydetector.checkers.info.AnalysisInProgress;
import org.mutabilitydetector.checkers.info.CyclicReferences;
import org.mutabilitydetector.checkers.info.InformationRetrievalRunner;
import org.mutabilitydetector.checkers.info.MutableTypeInformation;
import org.mutabilitydetector.classloading.CachingAnalysisClassLoader;
import org.mutabilitydetector.classloading.ClassForNameWrapper;
import org.mutabilitydetector.locations.Dotted;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mutabilitydetector.checkers.info.AnalysisDatabase.newAnalysisDatabase;
import static org.mutabilitydetector.config.HardcodedResultsUsage.DIRECTLY_IN_ASSERTION;

public final class DefaultCachingAnalysisSession implements AnalysisSession {

    private final MutabilityCheckerFactory checkerFactory;
    private final CheckerRunnerFactory checkerRunnerFactory;
    private final AnalysisDatabase database;
    private final AsmVerifierFactory verifierFactory;
    private final Configuration configuration;
    private final CyclicReferences cyclicReferences;
    private final Cache<Dotted, AnalysisResult> analysedClasses;

    private DefaultCachingAnalysisSession(CheckerRunnerFactory checkerRunnerFactory,
                                          MutabilityCheckerFactory checkerFactory,
                                          AsmVerifierFactory verifierFactory,
                                          Configuration configuration) {
        this.checkerRunnerFactory = checkerRunnerFactory;
        this.checkerFactory = checkerFactory;
        this.verifierFactory = verifierFactory;
        this.configuration = configuration;
        this.cyclicReferences = new CyclicReferences();
        this.analysedClasses = CacheBuilder.newBuilder().recordStats().build();
        this.analysedClasses.putAll(hardcodedResultsForDirectAssertion(configuration));

        InformationRetrievalRunner informationRetrievalRunner = new InformationRetrievalRunner(this, checkerRunnerFactory.createRunner());
        this.database = newAnalysisDatabase(informationRetrievalRunner);
    }

    private Map<Dotted, AnalysisResult> hardcodedResultsForDirectAssertion(Configuration configuration) {
        return configuration.howToUseHardcodedResults() == DIRECTLY_IN_ASSERTION
                ? configuration.hardcodedResults()
                : Collections.<Dotted, AnalysisResult>emptyMap();
    }

    public static AnalysisSession createWithGivenClassPath(ClassPath classpath,
                                                           CheckerRunnerFactory checkerRunnerFactory,
                                                           MutabilityCheckerFactory checkerFactory,
                                                           AsmVerifierFactory verifierFactory,
                                                           Configuration configuration) {
        return createWithGivenClassPath(classpath, configuration, verifierFactory);
    }


    /**
     * Creates an analysis session based suitable for runtime analysis.
     * <p>
     * For analysis, classes will be accessed through the runtime classpath.
     *
     * @param configuration custom configuration for analysis.
     * @return AnalysisSession for runtime analysis.
     * @see ConfigurationBuilder
     */
    public static AnalysisSession createWithCurrentClassPath(Configuration configuration) {
        ClassPath classpath = new ClassPathFactory().createFromJVM();
        ClassLoadingVerifierFactory verifierFactory = new ClassLoadingVerifierFactory(new CachingAnalysisClassLoader(new ClassForNameWrapper()));
        return createWithGivenClassPath(classpath, configuration, verifierFactory);
    }

    @SuppressWarnings("deprecation")
    private static AnalysisSession createWithGivenClassPath(ClassPath classpath,
                                                            Configuration configuration,
                                                            AsmVerifierFactory verifierFactory) {
        return new DefaultCachingAnalysisSession(new ClassPathBasedCheckerRunnerFactory(classpath, configuration.exceptionPolicy()),
                new MutabilityCheckerFactory(configuration.reassignedFieldAlgorithm(), configuration.immutableContainerClasses()),
                verifierFactory,
                configuration);
    }

    @Override
    public AnalysisResult resultFor(Dotted className) {
        return requestAnalysis(className, AnalysisInProgress.noAnalysisUnderway());
    }

    @Override
    public AnalysisResult processTransitiveAnalysis(Dotted className, AnalysisInProgress analysisInProgress) {
        return requestAnalysis(className, analysisInProgress);
    }

    private AnalysisResult requestAnalysis(Dotted className, AnalysisInProgress analysisInProgress) {
        AnalysisResult existingResult = analysedClasses.getIfPresent(className);
        if (existingResult != null) {
            return existingResult;
        }

        MutableTypeInformation mutableTypeInformation = new MutableTypeInformation(this, configuration, cyclicReferences);

        AllChecksRunner allChecksRunner = new AllChecksRunner(checkerFactory,
                checkerRunnerFactory,
                verifierFactory,
                className);

        AnalysisResult result = allChecksRunner.runCheckers(
                ImmutableList.copyOf(getResults()),
                database,
                mutableTypeInformation,
                analysisInProgress);

        return addAnalysisResult(result);
    }

    private AnalysisResult addAnalysisResult(AnalysisResult result) {
        analysedClasses.put(result.className, result);
        return result;
    }

    @Override
    public Collection<AnalysisResult> getResults() {
        return Collections.unmodifiableCollection(this.analysedClasses.asMap().values());
    }

    @Override
    public Map<Dotted, AnalysisResult> resultsByClass() {
        return Collections.unmodifiableMap(analysedClasses.asMap());
    }

    @Override
    public Collection<AnalysisError> getErrors() {
        return analysedClasses.asMap().values().stream()
                .flatMap(r -> r.errors.stream())
                .collect(Collectors.toList());
    }
}
