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

import static java.lang.String.format;
import static org.mutabilitydetector.checkers.AccessModifierQuery.type;
import static org.mutabilitydetector.checkers.info.Dotted.fromSlashedString;

import org.mutabilitydetector.checkers.AbstractMutabilityChecker;
import org.mutabilitydetector.checkers.MutabilityAnalysisException;
import org.mutabilitydetector.checkers.info.Dotted;
import org.objectweb.asm.Opcodes;

public class TypeStructureInformationChecker extends AbstractMutabilityChecker {

	private final Dotted className;
	private Boolean result;
	
	private TypeStructureInformationChecker(Dotted className) {
		this.className = className;
	}
	
	public static TypeStructureInformationChecker newChecker(Dotted className) {
		return new TypeStructureInformationChecker(className);
	}

	public boolean isAbstract() {
		return result;
	}
	
	@Override public void visit(int version, int access, String name, String signature, String superName,
			String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);
		
		checkIsVisitingCorrectClass();
		
		storeIsAbstract(access);
	}

	private void checkIsVisitingCorrectClass() {
		Dotted expectToVisit = fromSlashedString(ownerClass);
		if(!expectToVisit.equals(className)) {
			String message = format("Programming error: Expected to visit [%s], but am visiting [%s].", 
					className, expectToVisit);
			throw new MutabilityAnalysisException(message);
		}
	}

	private void storeIsAbstract(int access) {
		result = type(access).is(Opcodes.ACC_ABSTRACT);
	}

}
