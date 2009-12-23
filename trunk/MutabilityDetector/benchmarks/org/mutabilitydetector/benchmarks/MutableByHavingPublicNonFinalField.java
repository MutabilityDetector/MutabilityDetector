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
