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

import static org.mutabilitydetector.checkers.info.Dotted.dotted;

import org.mutabilitydetector.ClassNameConvertor;

public class ClassIdentifier {

	private Dotted dotted;
	
	private ClassIdentifier(Dotted className) {
		this.dotted = className;
	}
	
	public Dotted asDotted() { return dotted; }
	
	public static ClassIdentifier forClass(Dotted className) {
		return new ClassIdentifier(className);
	}
	
	public static ClassIdentifier forClass(Slashed className) {
		String slashed = className.asString();
		String dottedString = new ClassNameConvertor().dotted(slashed);
		
		return forClass(dotted(dottedString));
	}
}
