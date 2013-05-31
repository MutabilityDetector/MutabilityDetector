package org.mutabilitydetector.benchmarks;
 
public class ImmutableByHavingOnlyAPrivateConstructorUsingTheBuilderPattern {
  private final String field;
	
	// usual method of making a class immutable 
	// - make its constructor private: ref EffectiveJava
	private ImmutableByHavingOnlyAPrivateConstructorUsingTheBuilderPattern (String field) {
		this.field = field;
	}
	
	public String getField() {
		return field;
	}
	
	// inner Builder class
	public static class Builder {
		public ImmutableByHavingOnlyAPrivateConstructorUsingTheBuilderPattern build() {
			// this new OnlyPrivateConstructors() is fooling mutability detector
			// it thinks OnlyPrivateConstructors() is no longer immutable due to the
			// ability to call new to create an instance of OnlyPrivateConstructors.
			return new ImmutableByHavingOnlyAPrivateConstructorUsingTheBuilderPattern("hi");
		}
	}
}