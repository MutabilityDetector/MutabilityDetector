package org.mutabilitydetector;

import javax.annotation.concurrent.Immutable;


public interface AnalysisErrorReporter {

    void addAnalysisError(AnalysisError error);

    @Immutable
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