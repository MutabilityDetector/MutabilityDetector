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

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mutabilitydetector.AnalysisResult.definitelyImmutable;
import static org.mutabilitydetector.IAnalysisSession.IsImmutable.DEFINITELY_NOT;
import static org.mutabilitydetector.MutabilityReason.ABSTRACT_TYPE_TO_FIELD;
import static org.mutabilitydetector.locations.Dotted.dotted;

import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.CheckerReasonDetail;
import org.mutabilitydetector.locations.ClassLocation;

public class AllowedIfOtherClassIsImmutableTest {

	private static ClassLocation unusedClassLocation = null;
	private AllowedIfOtherClassIsImmutable matcher;
	
	@Test public void matchesIfResultIsDefinitelyImmutableAlready() throws Exception {
		AnalysisResult result = definitelyImmutable("some.immutable.class"); 
		matcher = new AllowedIfOtherClassIsImmutable(dotted("some.mutable.class"));
		assertThat(matcher.allowedReasons(result).size(), is(0));
	}

	@Test public void matchesWhenResultIsNotImmutableOnlyDueToAssigningAbstractTypeToField() throws Exception {
		CheckerReasonDetail reason = new CheckerReasonDetail(
				"Field can have an abstract type [some.mutable.class] assigned to it.",
				unusedClassLocation, ABSTRACT_TYPE_TO_FIELD);
		AnalysisResult result = new AnalysisResult("possibly.immutable.class", DEFINITELY_NOT, reason);
		matcher = new AllowedIfOtherClassIsImmutable(dotted("some.mutable.class"));
		
		assertThat(matcher.allowedReasons(result), hasItem(reason));
	}
	
}
