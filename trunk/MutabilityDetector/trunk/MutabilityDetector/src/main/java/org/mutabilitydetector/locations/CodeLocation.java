/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector.locations;

public interface CodeLocation<T extends CodeLocation<T>> extends Comparable<T> {

	String typeName();

	public abstract String prettyPrint();
	
}
