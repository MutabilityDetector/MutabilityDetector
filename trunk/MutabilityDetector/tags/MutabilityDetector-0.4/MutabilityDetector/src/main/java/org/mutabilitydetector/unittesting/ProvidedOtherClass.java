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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hamcrest.Description;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.CheckerReasonDetail;
import org.mutabilitydetector.locations.Dotted;
import org.mutabilitydetector.unittesting.matchers.AllowedIfOtherClassIsImmutable;
import org.mutabilitydetector.unittesting.matchers.AllowedReasonCollector;
import org.mutabilitydetector.unittesting.matchers.AnalysisResultMatcher;

public class ProvidedOtherClass extends AnalysisResultMatcher {

	private final Dotted dottedClassName;
	private final Set<AllowedReasonCollector> allowedReasonCollectors = new HashSet<AllowedReasonCollector>();

	public ProvidedOtherClass(Dotted dottedClassName) {
		this.dottedClassName = dottedClassName;
	}


	public ProvidedOtherClass isAlsoImmutable() {
		AllowedIfOtherClassIsImmutable allowed = new AllowedIfOtherClassIsImmutable(dottedClassName);
		allowedReasonCollectors.add(allowed);
		return this;
	}


	@Override protected boolean matchesSafely(AnalysisResult analysisResult, Description mismatchDescription) {
		Collection<CheckerReasonDetail> allowedReasons = new HashSet<CheckerReasonDetail>();
		for(AllowedReasonCollector collector: allowedReasonCollectors) {
			allowedReasons.addAll(collector.allowedReasons(analysisResult));
		}
		
		Collection<CheckerReasonDetail> actualReasons = new HashSet<CheckerReasonDetail>(analysisResult.reasons);
		
		actualReasons.removeAll(allowedReasons);
		return  (actualReasons.size() == 0);
	}

	@Override public void describeTo(Description description) {
		throw new UnsupportedOperationException("Not yet implemented.");
	}
}
