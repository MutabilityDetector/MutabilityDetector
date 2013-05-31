package org.mutabilitydetector.benchmarks;

import java.util.ArrayList;
import java.util.List;

public final class WrapsCollectionUsingNonWhitelistedMethod {
    public final List<Integer> numbers;

    public WrapsCollectionUsingNonWhitelistedMethod(List<Integer> numbers) {
        this.numbers = wrapInUnmodifiable(new ArrayList<Integer>(numbers));
    }

    private List<Integer> wrapInUnmodifiable(ArrayList<Integer> copy) {
        return copy;
    }
    
}
