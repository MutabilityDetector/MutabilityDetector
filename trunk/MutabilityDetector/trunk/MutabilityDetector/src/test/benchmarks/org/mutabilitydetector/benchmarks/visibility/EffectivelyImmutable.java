package org.mutabilitydetector.benchmarks.visibility;

public final class EffectivelyImmutable {

    public final int effectivelyFinal;
    
    public EffectivelyImmutable(int x) {
        this.effectivelyFinal = x;
    }
    
    public int getEffectivelyFinal() {
        return effectivelyFinal;
    }
}
