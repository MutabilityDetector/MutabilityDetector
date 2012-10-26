package org.mutabilitydetector;

import org.mutabilitydetector.locations.Dotted;

import com.google.common.base.Optional;

public interface Configuration {

    Optional<AnalysisResult> hardcodedResultFor(Dotted className);

}