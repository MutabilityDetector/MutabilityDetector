package org.mutabilitydetector.benchmarks.circular;

@SuppressWarnings("hiding") 
public class AssignsItselfToField {
    private AssignsItselfToField other;

    public AssignsItselfToField(AssignsItselfToField other) {
        this.other = other;
    }
    
    public void assignsItselfInOtherMethod(AssignsItselfToField other) {
        this.other = other;
    }
    
    void assignsFieldOfOtherInstanceOfSelf(AssignsItselfToField other) {
        other.other = this;
        other.other.other = null;
    }
}