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



import static org.hamcrest.MatcherAssert.assertThat;
import static org.mutabilitydetector.TestUtil.analysisDatabase;
import static org.mutabilitydetector.TestUtil.testingVerifierFactory;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;

import org.junit.Test;
import org.mutabilitydetector.benchmarks.ImmutableExample;
import org.mutabilitydetector.checkers.AllChecksRunner;
import org.mutabilitydetector.checkers.CheckerRunnerFactory;
import org.mutabilitydetector.checkers.ClassPathBasedCheckerRunnerFactory;
import org.mutabilitydetector.checkers.MutabilityCheckerFactory;
import org.mutabilitydetector.checkers.MutabilityCheckerFactory.ReassignedFieldAnalysisChoice;
import org.mutabilitydetector.checkers.info.AnalysisInProgress;
import org.mutabilitydetector.checkers.info.CyclicReferences;
import org.mutabilitydetector.checkers.info.MutableTypeInformation;
import org.mutabilitydetector.locations.Dotted;

import java.util.Collections;

public class AnalysisSessionTest {

    private final Dotted immutableClass = Dotted.fromClass(ImmutableExample.class);
    
    @Test
    public void analysisOfImmutableExampleWillBeRegistered() throws Exception {
        AnalysisSession analysisSession = TestUtil.testAnalysisSession();
        AnalysisErrorReporter errorReporter = analysisSession.errorReporter();
        MutabilityCheckerFactory checkerFactory = new MutabilityCheckerFactory(ReassignedFieldAnalysisChoice.LAZY_INITIALISATION_ANALYSIS, Collections.<Dotted>emptySet());
        CheckerRunnerFactory checkerRunnerFactory = new ClassPathBasedCheckerRunnerFactory(null, null);
        MutableTypeInformation mutableTypeInformation = new MutableTypeInformation(
                analysisSession,
                Configurations.NO_CONFIGURATION,
                CyclicReferences.newEmptyMutableInstance());
        AnalysisInProgress analysisInProgress = AnalysisInProgress.noAnalysisUnderway();

        AllChecksRunner checker = new AllChecksRunner(checkerFactory, 
                checkerRunnerFactory, 
                testingVerifierFactory(), 
                immutableClass);

        checker.runCheckers(analysisSession, errorReporter, analysisDatabase(), mutableTypeInformation, analysisInProgress);

        AnalysisResult result = analysisSession.resultFor(immutableClass);
        assertThat(result, areImmutable());
    }

    @Test
    public void analysisWillBeRunForClassesWhenQueriedOnImmutableStatus() throws Exception {
        AnalysisSession analysisSession = TestUtil.testAnalysisSession();
        AnalysisResult result = analysisSession.resultFor(immutableClass);
        assertThat(result, areImmutable());
    }

    
}
