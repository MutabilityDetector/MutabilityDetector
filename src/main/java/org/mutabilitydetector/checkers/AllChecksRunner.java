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
package org.mutabilitydetector.checkers;

import static com.google.common.collect.Iterables.addAll;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

import java.util.Collection;
import java.util.Map;

import org.mutabilitydetector.AnalysisErrorReporter;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.asmoverride.AsmVerifierFactory;
import org.mutabilitydetector.checkers.info.AnalysisDatabase;
import org.mutabilitydetector.checkers.info.MutableTypeInformation;
import org.mutabilitydetector.locations.Dotted;

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

    public AnalysisResult runCheckers(AnalysisSession analysisSession,
                                      AnalysisErrorReporter errorReporter,
                                      AnalysisDatabase database,
                                      MutableTypeInformation mutableTypeInformation) {
        Map<IsImmutable, Integer> results = newHashMap();
        Collection<MutableReasonDetail> reasons = newArrayList();

        Iterable<AsmMutabilityChecker> checkers = factory.createInstances(database, verifierFactory,
                mutableTypeInformation);

        for (AsmMutabilityChecker checker : checkers) {
            CheckerResult checkerResult = checkerRunnerFactory.createRunner().run(checker, toAnalyse, errorReporter,
                    analysisSession.getResults());
            results.put(checkerResult.isImmutable, getNewCount(results, checkerResult.isImmutable));
            addAll(reasons, checkerResult.reasons);
        }

        IsImmutable isImmutable = new ResultCalculator().calculateImmutableStatus(results);

        return AnalysisResult.analysisResult(toAnalyse.asString(), isImmutable, reasons);
    }

    private Integer getNewCount(Map<IsImmutable, Integer> results, IsImmutable result) {
        Integer oldCount = results.get(result);
        if (oldCount == null) oldCount = 0;
        return (oldCount + 1);
    }

}
