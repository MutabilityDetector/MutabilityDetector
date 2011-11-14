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

package org.mutabilitydetector.checkers.info;

import org.mutabilitydetector.CheckerRunner;
import org.mutabilitydetector.IAnalysisSession;
import org.mutabilitydetector.checkers.IMutabilityChecker;
import org.mutabilitydetector.checkers.ISessionCheckerRunner;
import org.mutabilitydetector.locations.ClassIdentifier;

public class SessionCheckerRunner implements ISessionCheckerRunner {

    private final CheckerRunner checkerRunner;
    private final IAnalysisSession analysisSession;

    public SessionCheckerRunner(IAnalysisSession analysisSession, CheckerRunner checkerRunner) {
        this.analysisSession = analysisSession;
        this.checkerRunner = checkerRunner;
    }

    @Override
    public void run(IMutabilityChecker checker, ClassIdentifier classIdentifier) {
        checkerRunner.run(analysisSession, checker, classIdentifier.asDotted());
    }

}
