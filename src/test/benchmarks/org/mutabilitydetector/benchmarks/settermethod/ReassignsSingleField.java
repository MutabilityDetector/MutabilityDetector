package org.mutabilitydetector.benchmarks.settermethod;

public final class ReassignsSingleField {
    @SuppressWarnings("unused")
    private int reassigned;
    
    public void setReassigned(int reassigned) {
        this.reassigned = reassigned;
    }
    
}
