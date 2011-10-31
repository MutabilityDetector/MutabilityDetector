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

	public String prettyPrint() {
		return String.format("[Field: %s, Class: %s]", fieldName(), typeName());
	}

}
