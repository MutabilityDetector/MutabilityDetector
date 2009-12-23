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

import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.IAnalysisSession.IsImmutable;

public class TestUtil {
	public static IsImmutable getResultOfAnalysis(Class<?> toAnalyse) {
		IsImmutable result = new AnalysisSession().isImmutable(toAnalyse.getName());
		return result;
	}
}
