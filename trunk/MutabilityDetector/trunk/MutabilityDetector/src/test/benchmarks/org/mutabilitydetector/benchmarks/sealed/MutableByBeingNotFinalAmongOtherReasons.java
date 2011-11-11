package org.mutabilitydetector.benchmarks.sealed;

public class MutableByBeingNotFinalAmongOtherReasons {

    public int intField;
    
    public MutableByBeingNotFinalAmongOtherReasons() {

    }
    
    public void setIntField(int newIntField) {
        intField = newIntField;
    }
}
