package org.mutabilitydetector.benchmarks.mutabletofield.array;

import java.util.Arrays;

public final class ImmutableByDefensivelyCopyingAndGuardingArray {

    private final Integer[] defensivelyCopied;

    public ImmutableByDefensivelyCopyingAndGuardingArray(Integer[] unsafe) {
        defensivelyCopied = new Integer[unsafe.length];
        System.arraycopy(unsafe, 0, defensivelyCopied, 0, unsafe.length);
    }
    
    public ImmutableByDefensivelyCopyingAndGuardingArray(Integer[] unsafe, int length) {
        defensivelyCopied = Arrays.copyOf(unsafe, length);
    }

    public Integer first() {
        return defensivelyCopied[0];
    }
    
}
