/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector.checkers.info;

import org.mutabilitydetector.ClassNameConvertor;

public class MethodIdentifier {
	
	private final String dottedClassName;
	private final String methodDescriptor;

	public MethodIdentifier(String dottedClassName, String methodDescriptor) {
		this.dottedClassName = dottedClassName;
		this.methodDescriptor = methodDescriptor;

	}
	
	@Override public String toString() {
		return dottedClassName + "." + methodDescriptor;
	}
	
	

	@Override public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dottedClassName == null) ? 0 : dottedClassName.hashCode());
		result = prime * result + ((methodDescriptor == null) ? 0 : methodDescriptor.hashCode());
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
		MethodIdentifier other = (MethodIdentifier) obj;
		if (dottedClassName == null) {
			if (other.dottedClassName != null) {
				return false;
			}
		} else if (!dottedClassName.equals(other.dottedClassName)) {
			return false;
		}
		if (methodDescriptor == null) {
			if (other.methodDescriptor != null) {
				return false;
			}
		} else if (!methodDescriptor.equals(other.methodDescriptor)) {
			return false;
		}
		return true;
	}

	public static MethodIdentifier forMethod(Dotted className, String methodDescriptor) {
		return new MethodIdentifier(className.className, methodDescriptor);
	}

	public static MethodIdentifier forMethod(Slashed className, String methodDescriptor) {
		String dotted = new ClassNameConvertor().dotted(className.className);
		return new MethodIdentifier(dotted, methodDescriptor);
	}
	
	public static class Dotted {
		String className;
		private Dotted(String dottedClassName) {
			className = dottedClassName;
		}
	}
	
	public static Dotted dotted(String dottedClassName) {
		return new Dotted(dottedClassName);
	}

	public static class Slashed {
		String className;
		public Slashed(String slashedClassName) {
			className = slashedClassName;
		}
	}
	
	public static Slashed slashed(String slashedClassName) {
		return new Slashed(slashedClassName);
	}

	public String dottedClassName() {
		return dottedClassName;
	}

	public String methodDescriptor() {
		return methodDescriptor;
	}

}
