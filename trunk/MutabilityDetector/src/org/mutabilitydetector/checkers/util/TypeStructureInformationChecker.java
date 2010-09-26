/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector.checkers.util;

import static org.mutabilitydetector.checkers.AccessModifierQuery.type;

import java.util.HashMap;
import java.util.Map;

import org.mutabilitydetector.checkers.AbstractMutabilityChecker;
import org.mutabilitydetector.checkers.info.Dotted;
import org.objectweb.asm.Opcodes;

public class TypeStructureInformationChecker extends AbstractMutabilityChecker {

	private Map<Dotted, Boolean> isAbstractMap = new HashMap<Dotted, Boolean>();
	
	private TypeStructureInformationChecker() { }
	
	public static TypeStructureInformationChecker newChecker() {
		return new TypeStructureInformationChecker();
	}

	public boolean isAbstract(Dotted className) {
		return isAbstractMap.get(className);
	}
	
	@Override public void visit(int version, int access, String name, String signature, String superName,
			String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);
		storeIsAbstract(access);
	}

	private void storeIsAbstract(int access) {
		boolean isAbstract = type(access).is(Opcodes.ACC_ABSTRACT);
		Dotted className = Dotted.fromSlashedString(ownerClass);
		isAbstractMap.put(className, isAbstract);
	}

}
