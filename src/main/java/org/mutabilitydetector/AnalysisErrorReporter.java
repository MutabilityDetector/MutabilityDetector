package org.mutabilitydetector;


public interface AnalysisErrorReporter {

    void addAnalysisError(AnalysisError error);

    public static final class AnalysisError {
        public final String checkerName;
        public final String description;
        public final String onClass;
    
        public AnalysisError(String onClass, String checkerName, String errorDescription) {
            this.onClass = onClass;
            this.checkerName = checkerName;
            this.description = errorDescription;
        }
    }
}