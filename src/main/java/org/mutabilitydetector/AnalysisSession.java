package org.mutabilitydetector;

import java.util.Map;

import org.mutabilitydetector.AnalysisErrorReporter.AnalysisError;
import org.mutabilitydetector.locations.Dotted;

public interface AnalysisSession {
    AnalysisResult resultFor(Dotted className);
    AnalysisErrorReporter errorReporter();
    
    Iterable<AnalysisResult> getResults();
    Map<Dotted, AnalysisResult> resultsByClass();
    
    Iterable<AnalysisError> getErrors();

}