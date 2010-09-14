/*
 * Mutability Detector
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * 
 * Further licensing information for this project can be found in
 * license/LICENSE.txt
 */

package org.mutabilitydetector.checkers;

/**
 * Used to check for the existence of an access flag used in ASM visitors.
 * 
 * The class is designed to be used in a fluent way, a typical usage would be:
 * 
 * <code>
 * import AccessModifierQuery.doesAccess;
 * import Opcodes.ACC_STATIC;
 * ...
 * {@link #method(access)}.is(ACC_STATIC);
 * </code>
 * 
 */
public class AccessModifierQuery {
	private int access;

	private AccessModifierQuery(int access) {
		this.access = access;
	}

	private boolean includesAccess(int access) {
		return (this.access & access) != 0;
	}

	public boolean is(int flag) {
		return includesAccess(flag);
	}

	public static AccessModifierQuery method(int access) {
		return new AccessModifierQuery(access);
	}
}
