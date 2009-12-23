package org.mutabilitydetector.benchmarks;

public final class ImmutableWithIndirectlyConstructedFieldInConcreteType {

	@SuppressWarnings("unused")
	private final CharSequence name;
	
	public ImmutableWithIndirectlyConstructedFieldInConcreteType() {
		this.name = FieldFactory.getNewName();
	}
}
