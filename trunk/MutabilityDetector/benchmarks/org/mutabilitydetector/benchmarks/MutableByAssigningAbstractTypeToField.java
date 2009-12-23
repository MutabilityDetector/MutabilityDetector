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

public final class MutableByAssigningAbstractTypeToField {

	@SuppressWarnings("unused")
	private AbstractStringContainer nameContainer;
	
	public MutableByAssigningAbstractTypeToField(AbstractStringContainer abstractNameContainer) {
		nameContainer = abstractNameContainer;
	}
	
	abstract class AbstractStringContainer {
		protected String name;
	}
	
	final class StringContainer extends AbstractStringContainer {
		
	}
}
