/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */
package org.mutabilitydetector.benchmarks.circular;

public final class CircularReferenceClasses {}

@SuppressWarnings("unused")
final class ImmutableClassA {
	private ImmutableClassB circularRef;
}

@SuppressWarnings("unused")
final class ImmutableClassB {
	private ImmutableClassA  circularRef;
}
