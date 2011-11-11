package org.mutabilitydetector.benchmarks.mutabletofield;

public class MutableByAssigningPrimitiveArrayToField {

    @SuppressWarnings("unused")
    private final int[] intArray;

    public MutableByAssigningPrimitiveArrayToField(int[] intArray) {
        this.intArray = intArray;
    }
    
}
