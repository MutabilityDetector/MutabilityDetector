package se.mutabilitydetector.benchmarks;

public class MutableByNoCopyOfIndirectlyConstructedField {

	@SuppressWarnings("unused")
	private CharSequence name;

	public MutableByNoCopyOfIndirectlyConstructedField(IFieldFactory fieldFactory) {
		this.name = fieldFactory.getName();
	}
	
}

interface IFieldFactory {
	public CharSequence getName();
}

final class FieldFactory implements IFieldFactory {
	public CharSequence getName() {
		return "name";
	}
	
	public static CharSequence getNewName() {
		return "name";
	}
}