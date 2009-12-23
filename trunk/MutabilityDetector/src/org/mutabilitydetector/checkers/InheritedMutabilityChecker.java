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
import static org.mutabilitydetector.IAnalysisSession.IsImmutable.DEFINITELY_NOT;

import org.mutabilitydetector.IAnalysisSession;

public class InheritedMutabilityChecker extends AbstractMutabilityChecker {

	private IAnalysisSession analysisSession;
	
	public InheritedMutabilityChecker(IAnalysisSession analysisSession) {
		this.analysisSession = analysisSession;
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);
		if(analysisSession.isImmutable(superName).equals(DEFINITELY_NOT)) {
			result = DEFINITELY_NOT;
		} else {
			result = DEFINITELY;
		}
	}

}
