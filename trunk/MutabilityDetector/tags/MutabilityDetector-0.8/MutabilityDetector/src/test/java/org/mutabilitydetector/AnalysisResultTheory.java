package org.mutabilitydetector;

import org.mutabilitydetector.IsImmutable;

public class AnalysisResultTheory {
    public final IsImmutable expected;
    public final Class<?> clazz;
    public AnalysisResultTheory(Class<?> clazz, IsImmutable expected) {
        this.expected = expected;
        this.clazz = clazz;
    }
    
    public static AnalysisResultTheory of(Class<?> clazz, IsImmutable toBe) {
        return new AnalysisResultTheory(clazz, toBe);
    }
}