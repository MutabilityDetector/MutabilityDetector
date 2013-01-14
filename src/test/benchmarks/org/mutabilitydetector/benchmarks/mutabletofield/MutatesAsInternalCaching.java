package org.mutabilitydetector.benchmarks.mutabletofield;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class MutatesAsInternalCaching {
    private final String myString;
    private final String otherString;
    private int lengthWhenConcatenated;
    public MutatesAsInternalCaching(String myString, String otherString) {
        this.myString = myString;
        this.otherString = otherString;
    }
    
    public int getConcatenatedLength() {
        if (lengthWhenConcatenated == 0) {
            lengthWhenConcatenated = myString.concat(otherString).length();
        }
        return lengthWhenConcatenated;
    }
    
}
