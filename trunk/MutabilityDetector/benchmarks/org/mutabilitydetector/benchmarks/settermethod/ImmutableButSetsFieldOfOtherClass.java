/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector.benchmarks.settermethod;

/**
 * This is for checking against a silly mistake in analysis, where only a field
 * being set is being checked for, regardless of what object holds the field.
 */
public class ImmutableButSetsFieldOfOtherClass {

	public void isThisASetterMethod(AssignMyField otherObject) {
		otherObject.reassignable = 42;
	}
	
}

class AssignMyField {
	public int reassignable;
}