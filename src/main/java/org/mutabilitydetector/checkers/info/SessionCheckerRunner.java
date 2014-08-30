package org.mutabilitydetector.checkers.info;

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



import org.mutabilitydetector.AnalysisErrorReporter;
import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.checkers.AsmMutabilityChecker;
import org.mutabilitydetector.checkers.AsmSessionCheckerRunner;
import org.mutabilitydetector.checkers.CheckerRunner;
import org.mutabilitydetector.locations.ClassIdentifier;

public class SessionCheckerRunner implements AsmSessionCheckerRunner {

    private final CheckerRunner checkerRunner;
    private final AnalysisSession analysisSession;
    private final AnalysisErrorReporter analysisErrorReporter;

    public SessionCheckerRunner(AnalysisSession analysisSession, CheckerRunner checkerRunner) {
        this.analysisSession = analysisSession;
        this.analysisErrorReporter = analysisSession.errorReporter();
        this.checkerRunner = checkerRunner;
    }

    @Override
    public void run(AsmMutabilityChecker checker, ClassIdentifier classIdentifier) {
        checkerRunner.run(checker, classIdentifier.asDotted(), analysisErrorReporter, analysisSession.getResults());
    }

}
