/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector.checkers.info;

public class Slashed extends ClassName {
	
	private Slashed(String slashedClassName) {
		super(slashedClassName);
	}
	
	public static Slashed slashed(String slashedClassName) {
		return new Slashed(slashedClassName);
	}
}