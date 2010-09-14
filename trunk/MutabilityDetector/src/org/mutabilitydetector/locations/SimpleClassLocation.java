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

import org.mutabilitydetector.ClassNameConvertor;
import org.mutabilitydetector.SourceLocation;

public class SimpleClassLocation implements SourceLocation {

	private final String dottedClassName;

	/**
	 * @param dottedClassName
	 */
	public SimpleClassLocation(String dottedClassName) {
		this.dottedClassName = dottedClassName;
	}

	@Override
	public String typeName() {
		return dottedClassName;
	}

	@Override
	public int compareTo(SourceLocation other) {
		return typeName().compareTo(other.typeName());
	}

	/**
	 * @param internalClassName
	 * @return
	 */
	public static SourceLocation fromInternalName(String internalClassName) {
		String dottedClassName = new ClassNameConvertor().dotted(internalClassName);
		return new SimpleClassLocation(dottedClassName);
	}


}
