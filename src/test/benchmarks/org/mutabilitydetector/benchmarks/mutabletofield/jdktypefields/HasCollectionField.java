package org.mutabilitydetector.benchmarks.mutabletofield.jdktypefields;

import java.util.List;

public final class HasCollectionField {
    private final List<String> myStrings;

    public HasCollectionField(List<String> strings) {
//        List<String> copy = copyIntoNewList(strings);
//        List<String> unmodifiable = wrapWithUnmodifiable(copy);
        this.myStrings = strings;
    }

    private List<String> wrapWithUnmodifiable(List<String> strings) {
        return strings;
    }

    private List<String> copyIntoNewList(List<String> strings) {
        return strings;
    }
    
    public List<String> getMyStrings() {
        return myStrings;
    }
}