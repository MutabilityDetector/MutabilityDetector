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