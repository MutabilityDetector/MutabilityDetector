package org.mutabilitydetector.benchmarks.mutabletofield.array;

public final class MutableByPublishingArray {

    private final Integer[] someInts;
    
    public MutableByPublishingArray() {
        someInts = new Integer[] { 3, 14 };
    }
    
    public Integer[] naughtyPublishingOfMutableComponent() {
        return someInts;
    }
}
