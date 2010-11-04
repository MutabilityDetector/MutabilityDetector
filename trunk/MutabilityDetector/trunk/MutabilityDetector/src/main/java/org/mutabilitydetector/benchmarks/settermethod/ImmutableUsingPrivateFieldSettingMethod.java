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
 * This class is immutable, but fields are set outwith the constructor. Notice
 * that the private field setter method is only called from within the
 * constructor, and while it does modify the fields, no changes to the instance
 * can be viewed by clients of the class.
 * 
 * This pattern can occur with serialisation of an immutable object.
 * 
 * Several examples were found within JodaTime.
 */
public class ImmutableUsingPrivateFieldSettingMethod {

	private int field1 = 1;
	private double field2 = 1.0d;
	private long field3 = 1l;

	public ImmutableUsingPrivateFieldSettingMethod() {
		setFields();
	}

	private void setFields() {
		field1++;
		field2++;
		field3++;
	}

	public int getField1() {
		return field1;
	}

	public double getField2() {
		return field2;
	}

	public long getField3() {
		return field3;
	}

}
