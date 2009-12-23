package se.mutabilitydetector.benchmarks.inheritance;

import se.mutabilitydetector.benchmarks.ImmutableExample;

public class ImmutableSupertype {
	@SuppressWarnings("unused")
	private final ImmutableExample immutableField;
	public ImmutableSupertype(ImmutableExample immutableField) {
		this.immutableField = immutableField;
	}	
}

class MutableSubtypeOfImmutableSupertype extends ImmutableSupertype {
	public Object reassignableField = new Object();
	public MutableSubtypeOfImmutableSupertype(ImmutableExample immutableField) {
		super(immutableField);
	}
}

final class ImmutableSubtypeOfImmutableSupertype extends ImmutableSupertype {
	@SuppressWarnings("unused")
	private final int immutableField = 2;
	public ImmutableSubtypeOfImmutableSupertype(ImmutableExample immutableField) {
		super(immutableField);
	}
	
}
