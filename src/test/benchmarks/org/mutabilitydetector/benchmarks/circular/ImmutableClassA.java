package org.mutabilitydetector.benchmarks.circular;

@SuppressWarnings("unused")
public final class ImmutableClassA {
    private final ImmutableClassB circularRef;
    
    public ImmutableClassA(ImmutableClassB classB) {
        circularRef = classB;
    }
}