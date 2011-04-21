/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector.unittesting;

import static org.mutabilitydetector.locations.Dotted.dotted;
import static org.mutabilitydetector.locations.Dotted.fromClass;

import org.mutabilitydetector.locations.Dotted;
import org.mutabilitydetector.unittesting.matchers.AnalysisResultMatcher;
import org.mutabilitydetector.unittesting.matchers.NoReasonAllowedMatcher;

public class AllowedReason {

	public AllowedReason() { }
	
	public static ProvidedOtherClass provided(String dottedClassName) {
		return allowedIfOtherClassIsImmutable(dotted(dottedClassName));
	}
	
	public static ProvidedOtherClass provided(Class<?> clazz) {
		return allowedIfOtherClassIsImmutable(fromClass(clazz));
	}
	
	private static ProvidedOtherClass allowedIfOtherClassIsImmutable(Dotted dottedClassName) {
		return new ProvidedOtherClass(dottedClassName);
	}
	

	public static AnalysisResultMatcher noneAllowed() {
		return new NoReasonAllowedMatcher();
	}

}
