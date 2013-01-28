package org.mutabilitydetector.benchmarks.mutabletofield.array;

import java.util.Arrays;
import java.util.List;

public final class MutableAsElementsOfArrayAreMutableAndPublished {

    private final List<?>[] defensivelyCopied;

    public MutableAsElementsOfArrayAreMutableAndPublished(List<?>[] unsafe) {
        defensivelyCopied = Arrays.copyOf(unsafe, unsafe.length);
    }

    public List<?> first() {
        return defensivelyCopied[0];
    }
    
}
