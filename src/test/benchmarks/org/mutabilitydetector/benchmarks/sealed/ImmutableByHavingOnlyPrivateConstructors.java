package org.mutabilitydetector.benchmarks.sealed;

/**
 * This class is not final, but it also cannot be subclassed, since all of its constructors are private.
 * 
 */
public class ImmutableByHavingOnlyPrivateConstructors {

    private final int something;
    
    private ImmutableByHavingOnlyPrivateConstructors(int something) {
        this.something = something;
       
    }
    
    private ImmutableByHavingOnlyPrivateConstructors() {
        this(43);
        
    }

    public int getSomething() {
        return something;
    }
    
    public static ImmutableByHavingOnlyPrivateConstructors newUp(int something) {
        return new ImmutableByHavingOnlyPrivateConstructors(something);
    }
    
}
