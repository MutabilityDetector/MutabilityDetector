package se.mutabilitydetector.benchmarks;

public final class MutableByHavingMutableFieldAssigned {
	private MutableExample mutableField; // Access level doesn't matter
	
	public MutableByHavingMutableFieldAssigned(MutableExample mutableField) {
		this.mutableField = mutableField;
	}
	
	public MutableExample getMutableField() {
		new PublishTarget().publishMutableField(mutableField);
		return mutableField;
	}
}

class MutableExample {
	public String name;
}

class PublishTarget {
	public void publishMutableField(MutableExample mutableField) {}
}