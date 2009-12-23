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

public class MutableByNoCopyOfIndirectlyConstructedField {

	@SuppressWarnings("unused")
	private CharSequence name;

	public MutableByNoCopyOfIndirectlyConstructedField(IFieldFactory fieldFactory) {
		this.name = fieldFactory.getName();
	}
	
}

interface IFieldFactory {
	public CharSequence getName();
}

final class FieldFactory implements IFieldFactory {
	public CharSequence getName() {
		return "name";
	}
	
	public static CharSequence getNewName() {
		return "name";
	}
}