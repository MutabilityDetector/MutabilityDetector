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

import org.mutabilitydetector.ClassNameConvertor;

public class Dotted extends ClassName {
	
	private Dotted(String className) {
		super(className);
	}
	
	public static Dotted dotted(String dottedClassName) {
		return new Dotted(dottedClassName);
	}
	
	public static Dotted fromSlashed(Slashed slashedClassName) {
		String converted = new ClassNameConvertor().dotted(slashedClassName.asString());
		return dotted(converted);
	}

}