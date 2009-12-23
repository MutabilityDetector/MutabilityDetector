/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */
package org.mutabilitydetector.checkers;

import static org.mutabilitydetector.IAnalysisSession.IsImmutable.DEFINITELY;
import static org.mutabilitydetector.IAnalysisSession.IsImmutable.MAYBE;

import org.objectweb.asm.Opcodes;

public class FinalClassChecker extends AbstractMutabilityChecker {

	
	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		if((access & Opcodes.ACC_FINAL) == 0) {
			result = MAYBE;
			reasons.add("Class is not declared final.");
		} else {
			result = DEFINITELY;
		}
		
	}

}
