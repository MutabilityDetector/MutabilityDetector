package org.mutabilitydetector;

import org.mutabilitydetector.AnalysisErrorReporter.AnalysisError;
import org.mutabilitydetector.locations.Dotted;

public interface AnalysisSession {
    AnalysisResult resultFor(Dotted className);
    AnalysisErrorReporter errorReporter();
    
    Iterable<AnalysisResult> getResults();
    Iterable<AnalysisError> getErrors();

}