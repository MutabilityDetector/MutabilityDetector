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

@SuppressWarnings("unused")
public class ImmutableButSetsPrivateFieldOfInstanceOfSelf {

	private int myField = 0;
	private ImmutableButSetsPrivateFieldOfInstanceOfSelf fieldOfSelfType = new ImmutableButSetsPrivateFieldOfInstanceOfSelf();
	
	public ImmutableButSetsPrivateFieldOfInstanceOfSelf setPrivateFieldOnInstanceOfSelf() {
		ImmutableButSetsPrivateFieldOfInstanceOfSelf i = new ImmutableButSetsPrivateFieldOfInstanceOfSelf();
		this.hashCode();
		i.myField = 10;
		this.equals(null);
		i.myField = 11;
		return i;
	}
		
}

class MutableBySettingFieldOnThisInstance {
	@SuppressWarnings("unused")
	private int myField = 0;
	
	public void setMyField(int newMyField) {
		this.myField = newMyField;
		
	}
}
