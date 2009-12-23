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

