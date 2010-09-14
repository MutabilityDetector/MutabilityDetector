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

public class MutableBySettingFieldOfField {
	
	public static FieldObject staticField = new FieldObject();

	FieldObject directField = new FieldObject();
	
	public void setIndirectField() {
		directField.indirectField = 43;
	}
	
	public void setDirectField() {
		directField = null;
	}
}

class FieldObject {
	public int indirectField = 42;
}
