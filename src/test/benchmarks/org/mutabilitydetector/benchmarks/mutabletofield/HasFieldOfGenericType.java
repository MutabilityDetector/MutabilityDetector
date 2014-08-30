package org.mutabilitydetector.benchmarks.mutabletofield;

public final class HasFieldOfGenericType<T> {

    public final T field;

    public HasFieldOfGenericType(T field) {
        this.field = field;
    }
}
