package org.mutabilitydetector.benchmarks.mutabletofield.array;

public final class ImmutableButHasUnmodifiedArrayAsField {
    private final int[] unmodifiedArray;

    public ImmutableButHasUnmodifiedArrayAsField() {
        this.unmodifiedArray = new int[] { 3, 14 };
    }
    
    public int getWhole() {
        return unmodifiedArray[0];
    }
    
    public int getFraction() {
        return unmodifiedArray[1];
    }
    
}
