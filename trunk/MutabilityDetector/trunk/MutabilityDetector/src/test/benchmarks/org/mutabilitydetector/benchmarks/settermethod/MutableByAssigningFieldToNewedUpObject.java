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

public class MutableByAssigningFieldToNewedUpObject {

	private String changeMe = "Begin like this";
	
	public void reassignMyField() {
		changeMe = new String("Haha!");
	}
	
	public String getChangeMe() {
		return changeMe;
	}
}
