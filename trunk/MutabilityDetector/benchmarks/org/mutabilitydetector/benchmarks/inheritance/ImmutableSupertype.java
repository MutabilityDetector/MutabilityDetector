/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */
package org.mutabilitydetector.benchmarks.inheritance;

import org.mutabilitydetector.benchmarks.ImmutableExample;

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
