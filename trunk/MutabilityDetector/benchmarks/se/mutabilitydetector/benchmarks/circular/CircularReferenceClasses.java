package se.mutabilitydetector.benchmarks.circular;

public final class CircularReferenceClasses {}

@SuppressWarnings("unused")
final class ImmutableClassA {
	private ImmutableClassB circularRef;
}

@SuppressWarnings("unused")
final class ImmutableClassB {
	private ImmutableClassA  circularRef;
}
