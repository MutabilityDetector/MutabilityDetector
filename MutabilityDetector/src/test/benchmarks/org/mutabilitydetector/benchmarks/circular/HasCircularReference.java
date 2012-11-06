package org.mutabilitydetector.benchmarks.circular;

public final class HasCircularReference {
    public final HasCircularReference h;

    public HasCircularReference(HasCircularReference other) {
        this.h = other;
    }
}