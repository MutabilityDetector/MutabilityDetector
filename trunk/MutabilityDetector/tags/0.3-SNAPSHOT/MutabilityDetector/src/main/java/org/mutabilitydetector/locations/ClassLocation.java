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


public class ClassLocation implements CodeLocation {

	private final String dottedClassName;

	/**
	 * @param dottedClassName
	 */
	public ClassLocation(String dottedClassName) {
		this.dottedClassName = dottedClassName;
	}

	@Override
	public String typeName() {
		return dottedClassName;
	}

	@Override
	public int compareTo(CodeLocation other) {
		return typeName().compareTo(other.typeName());
	}
	
	

	@Override public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dottedClassName == null) ? 0 : dottedClassName.hashCode());
		return result;
	}

	@Override public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ClassLocation other = (ClassLocation) obj;
		if (dottedClassName == null) {
			if (other.dottedClassName != null) {
				return false;
			}
		} else if (!dottedClassName.equals(other.dottedClassName)) {
			return false;
		}
		return true;
	}

	/**
	 * @param internalClassName
	 * @return
	 */
	public static CodeLocation fromInternalName(String internalClassName) {
		String dottedClassName = new ClassNameConvertor().dotted(internalClassName);
		return new ClassLocation(dottedClassName);
	}


}
