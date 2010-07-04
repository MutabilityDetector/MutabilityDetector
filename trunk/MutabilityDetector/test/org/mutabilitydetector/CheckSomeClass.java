/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector;

import static java.lang.String.format;

import org.mutabilitydetector.IAnalysisSession.AnalysisResult;

public class CheckSomeClass {

	public static void main(String[] args) {
		AnalysisResult analysisResult = TestUtil.getAnalysisResult(String.class);
		
		System.out.print(format("%s is %s", analysisResult.dottedClassName, analysisResult.isImmutable));
		System.out.println(TestUtil.formatReasons(analysisResult.reasons));
	}
	
	

}
