package org.mutabilitydetector.benchmarks.finalfield;

public final class HasNonFinalField {
    public final int isFinal;
    private int isNotFinal;
    
    public HasNonFinalField(int whatever) {
        isNotFinal = whatever;
        isFinal = whatever;
    }
    
    public int getIsNotFinal() {
        return isNotFinal;
    }
    
}
