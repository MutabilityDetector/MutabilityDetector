package org.mutabilitydetector.benchmarks.visibility;

public class AlmostEffectivelyImmutable {
    
    private int something;
    
    public AlmostEffectivelyImmutable(int something) {
        this.something = something;
    }
    
    public int getSomething() {
        return something;
    }

}
