/* 
 * Mutability Detector
 *
 * Copyright 2009 Graham Allan
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.mutabilitydetector.benchmarks.inheritance;

import org.mutabilitydetector.benchmarks.ImmutableExample;

public class MutableSupertype {
	public int mutableField;

	public MutableSupertype(int num) {
		this.mutableField = num;
	}
}

class MutableSubtypeOfMutableSupertype extends ImmutableSupertype {
	public Object reassignableField = new Object();

	public MutableSubtypeOfMutableSupertype(ImmutableExample immutableField) {
		super(immutableField);
	}
}

/*
 * Even if a subclass itself passes checks for immutability, if it's supertype
 * makes it mutable, it should fail the check.
 */
@SuppressWarnings("unused")
final class ImmutableSubtypeOfMutableSupertype extends MutableSupertype {
	private final int immutableField = 2;
	public ImmutableSubtypeOfMutableSupertype(int num) {
		super(num);
	}
}

@SuppressWarnings("unused")
final class ImmutableSubtypeWithNoSuperclass {
	private final int immutableField = 3;
}
