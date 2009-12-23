/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */
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
