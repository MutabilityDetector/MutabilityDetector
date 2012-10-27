package org.mutabilitydetector;

import java.util.Map;

import org.mutabilitydetector.locations.Dotted;

public interface Configuration {
    Map<Dotted, AnalysisResult> hardcodedResults();
}