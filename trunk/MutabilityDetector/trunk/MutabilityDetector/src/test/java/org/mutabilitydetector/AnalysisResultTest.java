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

import static org.mutabilitydetector.IAnalysisSession.IsImmutable.IMMUTABLE;

import java.util.ArrayList;

import org.junit.Test;
import org.mutabilitydetector.IAnalysisSession.IsImmutable;

public class AnalysisResultTest {

	@Test(expected = IllegalArgumentException.class) 
	public void throwsExceptionWhenNotImmutableAndNoReasonsGiven_varargs() throws Exception {
		new AnalysisResult("someclass", IsImmutable.NOT_IMMUTABLE);
	}
	
	@Test(expected = IllegalArgumentException.class) 
	public void throwsExceptionWhenNotImmutableAndNoReasonsGiven_collection() throws Exception {
		new AnalysisResult("someclass", IsImmutable.NOT_IMMUTABLE, new ArrayList<CheckerReasonDetail>());
	}
	
	@Test public void doesntThrowExceptionWhenImmutableAndNoReasonGiven() throws Exception {
		new AnalysisResult("someclass", IMMUTABLE);
	}
	
}
