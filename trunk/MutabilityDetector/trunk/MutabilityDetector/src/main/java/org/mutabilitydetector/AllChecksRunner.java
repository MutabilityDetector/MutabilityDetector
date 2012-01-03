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
package org.mutabilitydetector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.mutabilitydetector.checkers.AsmMutabilityChecker;
import org.mutabilitydetector.checkers.ResultCalculator;
import org.mutabilitydetector.locations.Dotted;

public class AllChecksRunner {

    private final Dotted toAnalyse;
    private final Collection<MutableReasonDetail> reasons = new ArrayList<MutableReasonDetail>();
    private final IMutabilityCheckerFactory factory;
    private final ICheckerRunnerFactory checkerRunnerFactory;

    public AllChecksRunner(IMutabilityCheckerFactory checkerFactory,
            ICheckerRunnerFactory checkerRunnerFactory,
            Dotted toAnalyse) {
        factory = checkerFactory;
        this.checkerRunnerFactory = checkerRunnerFactory;
        this.toAnalyse = toAnalyse;

    }

    public AnalysisResult runCheckers(IAnalysisSession analysisSession) {
        Map<IsImmutable, Integer> results = new HashMap<IsImmutable, Integer>();

        Collection<AsmMutabilityChecker> checkers = factory.createInstances(analysisSession);
        for (AsmMutabilityChecker checker : checkers) {
            checkerRunnerFactory.createRunner().run(analysisSession, checker, toAnalyse);
            IsImmutable result = checker.result();
            results.put(result, getNewCount(results, result));
            reasons.addAll(checker.reasons());
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
