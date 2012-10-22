package org.mutabilitydetector;

import javax.annotation.concurrent.Immutable;

import org.mutabilitydetector.AnalysisErrorReporter.AnalysisError;
import org.mutabilitydetector.locations.Dotted;

public interface AnalysisSession {
    RequestedAnalysis resultFor(Dotted className);
    AnalysisErrorReporter errorReporter();
    
    Iterable<AnalysisResult> getResults();
    Iterable<AnalysisError> getErrors();

    @Immutable
    public static final class RequestedAnalysis {
        public final AnalysisResult result;
        public final boolean analysisComplete;
        
        private RequestedAnalysis(AnalysisResult result) {
            this.result = result;
            this.analysisComplete = result != null;
        }
        
        public static RequestedAnalysis incomplete() {
            return new RequestedAnalysis(null);
        }
        
        public static RequestedAnalysis complete(AnalysisResult result) {
            return new RequestedAnalysis(result);
        }
    }


    
}