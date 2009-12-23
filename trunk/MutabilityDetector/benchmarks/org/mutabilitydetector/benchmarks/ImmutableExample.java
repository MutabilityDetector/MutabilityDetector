package org.mutabilitydetector.benchmarks;

public final class ImmutableExample {

	private final MutableFieldInterface name;
	
	public ImmutableExample(MutableFieldInterface name) {
		this.name = new ImmutableField(name);
	}
	
	public MutableFieldInterface getName() {
		return new ImmutableField(name);
	}
	
}

interface MutableFieldInterface {}
final class ImmutableField implements MutableFieldInterface {
	public ImmutableField(MutableFieldInterface possiblyMutable) {}
}

