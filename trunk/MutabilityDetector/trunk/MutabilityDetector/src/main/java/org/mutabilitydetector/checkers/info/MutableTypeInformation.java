package org.mutabilitydetector.checkers.info;

import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.AnalysisSession.RequestedAnalysis;
import org.mutabilitydetector.Configuration;
import org.mutabilitydetector.locations.Dotted;

public final class MutableTypeInformation {

    private final AnalysisSession analysisSession;
    private Configuration configuration;

    public MutableTypeInformation(AnalysisSession analysisSession, Configuration configuration) {
        this.analysisSession = analysisSession;
        this.configuration = configuration;
    }

    public RequestedAnalysis resultOf(Dotted dotted) {
        AnalysisResult hardcodedResult = configuration.hardcodedResults().get(dotted);
        if (hardcodedResult != null) {
            return RequestedAnalysis.complete(hardcodedResult);
        }
        
        return analysisSession.resultFor(dotted);
    }
    
}