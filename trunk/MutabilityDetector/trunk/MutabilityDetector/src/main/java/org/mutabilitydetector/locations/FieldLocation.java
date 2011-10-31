package org.mutabilitydetector.locations;

public class FieldLocation implements CodeLocation<FieldLocation> {

    private final String fieldName;
    private final ClassLocation ownerOfField;

    public FieldLocation(String fieldName, ClassLocation ownerOfField) {
        this.fieldName = fieldName;
        this.ownerOfField = ownerOfField;
    }

    public static FieldLocation fieldLocation(String fieldName, ClassLocation ownerOfField) {
        return new FieldLocation(fieldName, ownerOfField);
    }

    public String fieldName() {
        return fieldName;
    }

    public String typeName() {
        return ownerOfField.typeName();
    }

    @Override
    public int compareTo(FieldLocation other) {
        int comparingOwner = ownerOfField.compareTo(other.ownerOfField);
        int comparingFieldName = fieldName.compareTo(other.fieldName);
        return comparingOwner == 0 ? comparingFieldName : comparingOwner;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + fieldName.hashCode();
        result = prime * result + ownerOfField.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        FieldLocation other = (FieldLocation) obj;

        return fieldName.equals(other.fieldName) && ownerOfField.equals(other.ownerOfField);
    }

    public String prettyPrint() {
        return String.format("[Field: %s, Class: %s]", fieldName(), typeName());
    }

}
