package org.mutabilitydetector.benchmarks;

import java.util.Arrays;

public class MutableByHavingArrayTypeAsField {
	private final String names[];
	
	public MutableByHavingArrayTypeAsField(String... names) {
		this.names = Arrays.copyOf(names, names.length);
	}
	
	public void mutateArray() {
		names[0] = "Haha I've mutated this instance!";
	}
}
