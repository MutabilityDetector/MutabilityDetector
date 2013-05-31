package org.mutabilitydetector;

import java.util.Map;

import org.mutabilitydetector.CheckerRunner.ExceptionPolicy;
import org.mutabilitydetector.locations.Dotted;

public interface Configuration {
    Map<Dotted, AnalysisResult> hardcodedResults();

    ExceptionPolicy exceptionPolicy();
}