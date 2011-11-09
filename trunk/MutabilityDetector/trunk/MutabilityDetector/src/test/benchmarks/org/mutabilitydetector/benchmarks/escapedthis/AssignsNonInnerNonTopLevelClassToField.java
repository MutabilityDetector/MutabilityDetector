package org.mutabilitydetector.benchmarks.escapedthis;

@SuppressWarnings("unused")
public final class AssignsNonInnerNonTopLevelClassToField {
    private final NonTopLevel field = new NonTopLevel();
}

class NonTopLevel {
    
}