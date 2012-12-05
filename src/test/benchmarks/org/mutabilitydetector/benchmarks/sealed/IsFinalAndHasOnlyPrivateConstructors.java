package org.mutabilitydetector.benchmarks.sealed;

public final class IsFinalAndHasOnlyPrivateConstructors {
    private final long something;
    
    private IsFinalAndHasOnlyPrivateConstructors(int something) {
        this.something = something;
       
    }
    
    private IsFinalAndHasOnlyPrivateConstructors() {
        this(43);
        
    }

    public long getSomething() {
        return something;
    }
    
    public static IsFinalAndHasOnlyPrivateConstructors newUp(int something) {
        return new IsFinalAndHasOnlyPrivateConstructors(something);
    }
}
