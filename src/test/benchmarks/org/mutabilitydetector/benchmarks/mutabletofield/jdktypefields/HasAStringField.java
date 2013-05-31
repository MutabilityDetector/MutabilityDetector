package org.mutabilitydetector.benchmarks.mutabletofield.jdktypefields;

public final class HasAStringField {
    private final String stringField;

    public HasAStringField(String stringField) {
        this.stringField = stringField;
    }
    
    public String getStringField() {
        return stringField;
    }
    
}
