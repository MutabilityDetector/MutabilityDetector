package org.mutabilitydetector.checkers.info;

import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.AnalysisSession.RequestedAnalysis;
import org.mutabilitydetector.locations.Dotted;

public final class MutableTypeInformation {

    private final AnalysisSession analysisSession;

    public MutableTypeInformation(AnalysisSession analysisSession) {
        this.analysisSession = analysisSession;
    }

    public RequestedAnalysis resultOf(Dotted dotted) {
        return analysisSession.resultFor(dotted);
    }
    
}