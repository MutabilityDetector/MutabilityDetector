package org.mutabilitydetector.checkers.info;

import org.mutabilitydetector.IAnalysisSession;
import org.mutabilitydetector.IAnalysisSession.RequestedAnalysis;
import org.mutabilitydetector.locations.Dotted;

public final class MutableTypeInformation {

    private final IAnalysisSession analysisSession;

    public MutableTypeInformation(IAnalysisSession analysisSession) {
        this.analysisSession = analysisSession;
    }

    public RequestedAnalysis resultOf(Dotted dotted) {
        return analysisSession.resultFor(dotted);
    }
    
}