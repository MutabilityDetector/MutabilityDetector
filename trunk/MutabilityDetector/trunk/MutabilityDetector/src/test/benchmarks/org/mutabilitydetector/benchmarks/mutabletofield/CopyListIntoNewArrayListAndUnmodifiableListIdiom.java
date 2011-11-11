package org.mutabilitydetector.benchmarks.mutabletofield;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CopyListIntoNewArrayListAndUnmodifiableListIdiom {

    private final List<String> unmodifiable;
    
    public CopyListIntoNewArrayListAndUnmodifiableListIdiom(List<String> potentiallyMutatable) {
        this.unmodifiable = Collections.unmodifiableList(new ArrayList<String>(potentiallyMutatable));
    }
    
    public List<String> getUnmodifiable() {
        return unmodifiable;
    }
    
    public final static class StaticMethodDoesTheCopying {
        private final List<String> unmodifiable;

        private StaticMethodDoesTheCopying(List<String> unmodifiable) {
            this.unmodifiable = unmodifiable;
        }
        
        public static StaticMethodDoesTheCopying create(List<String> potentiallyMutatable) {
            return new StaticMethodDoesTheCopying(Collections.unmodifiableList(new ArrayList<String>(potentiallyMutatable)));
        }
         
        public List<String> getUnmodifiable() {
            return unmodifiable;
        }
        
    }
    
}
