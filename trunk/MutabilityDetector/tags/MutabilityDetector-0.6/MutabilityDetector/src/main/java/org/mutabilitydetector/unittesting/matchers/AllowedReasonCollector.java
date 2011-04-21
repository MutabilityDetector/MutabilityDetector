/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector.unittesting.matchers;

import java.util.Collection;

import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.CheckerReasonDetail;


public abstract class AllowedReasonCollector {
	
	abstract public Collection<CheckerReasonDetail> allowedReasons(AnalysisResult analysisResult);
}
