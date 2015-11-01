package org.mutabilitydetector.checkers.info;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import org.mutabilitydetector.locations.Dotted;

public final class AnalysisInProgress {
    private final ImmutableList<Dotted> inProgress;

    private AnalysisInProgress() {
        this.inProgress = ImmutableList.of();
    }

    private AnalysisInProgress(ImmutableList<Dotted> inProgress) {
        this.inProgress = inProgress;
    }

    public static AnalysisInProgress noAnalysisUnderway() { return new AnalysisInProgress(); }

    public boolean contains(Dotted clazz) { return inProgress.contains(clazz); }

    public AnalysisInProgress register(Dotted clazz) {
        return new AnalysisInProgress(ImmutableList.<Dotted>builder().addAll(inProgress).add(clazz).build());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnalysisInProgress that = (AnalysisInProgress) o;
        return Objects.equal(inProgress, that.inProgress);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(inProgress);
    }
}
