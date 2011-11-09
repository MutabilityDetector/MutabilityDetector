package org.mutabilitydetector.benchmarks.escapedthis;

@SuppressWarnings("unused")
public final class AssignsNonInnerNonTopLevelClassToField {
    private final NonTopLevel field = new NonTopLevel();
    private final int other;
    
    public AssignsNonInnerNonTopLevelClassToField(int other) {
        this.other = other;
    }
}

class NonTopLevel {
    
}