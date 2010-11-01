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

public class ClassName {
	private String asString;
	
	public ClassName(String className) {
		this.asString = className;
	}
	
	public String asString() {
		return asString;
	}
	
	@Override public String toString() {
		return asString();
	}

	@Override public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((asString == null) ? 0 : asString.hashCode());
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
		ClassName other = (ClassName) obj;
		if (asString == null) {
			if (other.asString != null) {
				return false;
			}
		} else if (!asString.equals(other.asString)) {
			return false;
		}
		return true;
	}
	
}
