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
import java.util.HashSet;

import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.CheckerReasonDetail;
import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.locations.Dotted;

public class AllowedIfOtherClassIsImmutable extends AllowedReasonCollector {

	private final Dotted className;

	public AllowedIfOtherClassIsImmutable(Dotted dottedClassName) {
		this.className = dottedClassName;
	}

	@Override public Collection<CheckerReasonDetail> allowedReasons(AnalysisResult analysisResult) {
		Collection<CheckerReasonDetail> allowed = new HashSet<CheckerReasonDetail>();
		
		for (CheckerReasonDetail checkerReasonDetail : analysisResult.reasons) {
			if(checkerReasonDetail.reason() == MutabilityReason.ABSTRACT_TYPE_TO_FIELD
					&& checkerReasonDetail.message().contains(className.asString())) {
				allowed.add(checkerReasonDetail);
			}
		}
		
		return allowed;
	}
}
