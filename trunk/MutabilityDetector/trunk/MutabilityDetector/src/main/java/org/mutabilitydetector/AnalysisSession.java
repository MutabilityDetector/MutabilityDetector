package org.mutabilitydetector;

import org.mutabilitydetector.locations.Dotted;

public interface AnalysisSession {
    RequestedAnalysis resultFor(Dotted className);
    
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