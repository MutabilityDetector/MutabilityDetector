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

/**
 * @author Graham Allan (grundlefleck@gmail.com)
 * @date 19 Apr 2010
 * 
 */
public class MutableByExtendingMutableType extends MutableByHavingPublicNonFinalField {

	public MutableByExtendingMutableType(String name) {
		super(name);
	}

}
