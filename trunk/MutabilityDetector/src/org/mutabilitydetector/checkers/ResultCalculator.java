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

import static java.lang.Integer.valueOf;
import static org.mutabilitydetector.IAnalysisSession.IsImmutable.DEFINITELY;
import static org.mutabilitydetector.IAnalysisSession.IsImmutable.DEFINITELY_NOT;
import static org.mutabilitydetector.IAnalysisSession.IsImmutable.MAYBE;
import static org.mutabilitydetector.IAnalysisSession.IsImmutable.PROBABLY;

import java.util.Map;

import org.mutabilitydetector.IAnalysisSession.IsImmutable;


public final class ResultCalculator {
	public IsImmutable calculateImmutableStatus(Map<IsImmutable, Integer> results) {
		IsImmutable isImmutable;
		int numDefinitely = getNumOfResult(results, DEFINITELY);
		int numProbably = getNumOfResult(results, PROBABLY);
		int numMaybe = getNumOfResult(results, MAYBE);
		int numDefinitelyNot = getNumOfResult(results, DEFINITELY_NOT);
		
		if(numDefinitelyNot > 0) {
			isImmutable = DEFINITELY_NOT; 
		} else if(numMaybe > 0) {
			isImmutable = MAYBE;
		} else if(numProbably > 0) {
			isImmutable = PROBABLY;
		} else if(numDefinitely > 0) {
			isImmutable = DEFINITELY;
		} else {
			isImmutable = numProbably > numMaybe ? PROBABLY : MAYBE;
		}
		
		return isImmutable;
	}

	private int getNumOfResult(Map<IsImmutable, Integer> results, IsImmutable resultType) {
		if(! results.containsKey(resultType)) { return valueOf(0); }
		
		Integer numOfResultType = valueOf(results.get(resultType));
		if(numOfResultType != null) {
			return numOfResultType.intValue();
		} else {
			return valueOf(0);
		}
	}
}
