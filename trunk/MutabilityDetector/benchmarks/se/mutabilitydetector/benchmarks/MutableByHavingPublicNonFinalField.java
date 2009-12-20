package se.mutabilitydetector.benchmarks;

public class MutableByHavingPublicNonFinalField {
	public String name;
	
	public MutableByHavingPublicNonFinalField(String name) {
		this.name = name;
	}
}

final class MutableByHavingProtectedNonFinalField {
	protected String name;
}

final class MutableByHavingDefaultVisibleNonFinalField {
	String name;
}

final class ImmutableWithPublicFinalField {
	public final String name = "";
}
