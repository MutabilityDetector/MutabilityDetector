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

import static org.mutabilitydetector.locations.ClassIdentifier.forClass;

import java.util.HashMap;
import java.util.Map;

import org.mutabilitydetector.checkers.ISessionCheckerRunner;
import org.mutabilitydetector.checkers.util.TypeStructureInformationChecker;
import org.mutabilitydetector.locations.Dotted;

public class TypeStructureInformation implements AnalysisInformation {

	private final ISessionCheckerRunner sessionCheckerRunner;
	private final Map<Dotted, Boolean> isAbstractMap = new HashMap<Dotted, Boolean>();

	public TypeStructureInformation(ISessionCheckerRunner sessionCheckerRunner) {
		this.sessionCheckerRunner = sessionCheckerRunner;
	}

	public boolean isTypeAbstract(Dotted className) {
		Boolean result = false;
		if (isAbstractMap.containsKey(className)) {
			result = isAbstractMap.get(className);
			
		} else {
			TypeStructureInformationChecker checker = TypeStructureInformationChecker.newChecker(className);
			sessionCheckerRunner.run(checker, forClass(className));
			result =  checker.isAbstract();
			isAbstractMap.put(className, result);
		}
		return result;
	}
}
