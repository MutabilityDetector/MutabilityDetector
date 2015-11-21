package org.mutabilitydetector.checkers;

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


import com.google.common.collect.ImmutableList;
import org.mutabilitydetector.AnalysisError;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.asmoverride.AsmVerifierFactory;
import org.mutabilitydetector.checkers.info.AnalysisDatabase;
import org.mutabilitydetector.checkers.info.AnalysisInProgress;
import org.mutabilitydetector.checkers.info.MutableTypeInformation;
import org.mutabilitydetector.locations.Dotted;

import java.util.Collection;
import java.util.Map;

import static com.google.common.collect.Iterables.addAll;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

public final class AllChecksRunner {

    private final MutabilityCheckerFactory factory;
    private final CheckerRunnerFactory checkerRunnerFactory;
    private final AsmVerifierFactory verifierFactory;
    private final Dotted toAnalyse;

    public AllChecksRunner(MutabilityCheckerFactory checkerFactory,
            CheckerRunnerFactory checkerRunnerFactory,
            AsmVerifierFactory verifierFactory,
            Dotted toAnalyse) {
        this.factory = checkerFactory;
        this.checkerRunnerFactory = checkerRunnerFactory;
        this.verifierFactory = verifierFactory;
        this.toAnalyse = toAnalyse;
    }

    public ResultAndErrors runCheckers(ImmutableList<AnalysisResult> knownResultsSoFar,
                                      AnalysisDatabase database,
                                      MutableTypeInformation mutableTypeInformation,
                                      AnalysisInProgress analysisInProgress) {
        Map<IsImmutable, Integer> results = newHashMap();
        Collection<MutableReasonDetail> reasons = newArrayList();
        Collection<AnalysisError> errors = newArrayList();

        Iterable<AsmMutabilityChecker> checkers = factory.createInstances(
                database,
                verifierFactory,
                mutableTypeInformation,
                analysisInProgress);

        CheckerRunner checkerRunner = checkerRunnerFactory.createRunner();

        for (AsmMutabilityChecker checker : checkers) {
            CheckerResult checkerResult = checkerRunner.run(checker, toAnalyse, knownResultsSoFar);
            results.put(checkerResult.isImmutable, getNewCount(results, checkerResult.isImmutable));
            addAll(reasons, checkerResult.reasons);
            addAll(errors, checkerResult.errors);
        }

        IsImmutable isImmutable = new ResultCalculator().calculateImmutableStatus(results);

        return new ResultAndErrors(
                AnalysisResult.analysisResult(toAnalyse, isImmutable, reasons),
                ImmutableList.copyOf(errors));
    }

    private Integer getNewCount(Map<IsImmutable, Integer> results, IsImmutable result) {
        Integer oldCount = results.get(result);
        if (oldCount == null) oldCount = 0;
        return (oldCount + 1);
    }

    /**
     * Refactoring cheat to save adding the errors into AnalysisResult, as that's a bigger change. Take a "snapshot" by
     * creating a new class to hold them both.
     */
    public static final class ResultAndErrors {
        public final AnalysisResult result;
        public final Collection<AnalysisError> errors;

        public ResultAndErrors(AnalysisResult result, Collection<AnalysisError> errors) {
            this.result = result;
            this.errors = errors;
        }
    }

}
