/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
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
