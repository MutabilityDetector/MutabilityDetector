package org.mutabilitydetector.benchmarks.circular;

@SuppressWarnings("unused")
public final class ImmutableClassB {
    private final ImmutableClassA circularRef;
    
    public ImmutableClassB(ImmutableClassA classA) {
        circularRef = classA;
    }
}