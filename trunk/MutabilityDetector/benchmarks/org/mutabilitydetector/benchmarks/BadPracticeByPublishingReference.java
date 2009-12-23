package org.mutabilitydetector.benchmarks;

public class BadPracticeByPublishingReference {
	private String name;
	
	public BadPracticeByPublishingReference() {
		name = "Scott";
	}
	
	public String getName() {
		return name;
	}
}
