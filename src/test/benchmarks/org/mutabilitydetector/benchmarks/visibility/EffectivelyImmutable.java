package org.mutabilitydetector.benchmarks.visibility;

public final class EffectivelyImmutable {

    private int effectivelyFinal;
    
    public EffectivelyImmutable(int x) {
        this.effectivelyFinal = x;
    }
    
    public int getEffectivelyFinal() {
        return effectivelyFinal;
    }
}
