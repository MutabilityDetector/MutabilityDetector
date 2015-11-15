package org.mutabilitydetector;

import org.mutabilitydetector.locations.Dotted;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class AnalysisError {
    public final String checkerName;
    public final String description;
    public final Dotted onClass;

    public AnalysisError(Dotted onClass, String checkerName, String errorDescription) {
        this.onClass = onClass;
        this.checkerName = checkerName;
        this.description = errorDescription;
    }
}
