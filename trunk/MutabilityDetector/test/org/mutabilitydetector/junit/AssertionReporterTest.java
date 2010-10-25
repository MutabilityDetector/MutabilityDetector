/*
 * Mutability Detector
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * 
 * Further licensing information for this project can be found in
 * license/LICENSE.txt
 */

package org.mutabilitydetector.junit;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mutabilitydetector.IAnalysisSession.IsImmutable.DEFINITELY;
import static org.mutabilitydetector.IAnalysisSession.IsImmutable.DEFINITELY_NOT;
import static org.mutabilitydetector.MutabilityReason.PUBLISHED_NON_FINAL_FIELD;

import java.util.Collection;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.mutabilitydetector.CheckerReasonDetail;
import org.mutabilitydetector.IAnalysisSession.AnalysisResult;
import org.mutabilitydetector.IAnalysisSession.IsImmutable;

public class AssertionReporterTest {

	private AssertionReporter reporter;

	@Before public void setUp() {
		reporter = new AssertionReporter();
	}

	@Test public void reporterDoesNotThrowAssertionErrorForImmutableResult() throws Exception {
		AnalysisResult analysisResult = AnalysisResult.definitelyImmutable("a.b.c");
		reporter.expectedImmutable(analysisResult);
	}

	@Test(expected = MutabilityAssertionError.class) 
	public void reporterThrowsExceptionForMutableResult() {
		AnalysisResult analysisResult = new AnalysisResult("a.b.c", DEFINITELY_NOT, unusedReasons());
		reporter.expectedImmutable(analysisResult);
	}

	@Test public void thrownExceptionContainsHelpfulMessage() throws Exception {
		CheckerReasonDetail reason = new CheckerReasonDetail("a reason the class is mutable", null,
				PUBLISHED_NON_FINAL_FIELD);

		AnalysisResult analysisResult = new AnalysisResult("d.e.f", DEFINITELY_NOT, asList(reason));
		try {
			reporter.expectedImmutable(analysisResult);
			fail("expected exception");
		} catch (MutabilityAssertionError e) {
			String expectedMessage = format("Expected class %s to be [%s] immutable, but was [%s] immutable.", 
					"d.e.f", DEFINITELY, DEFINITELY_NOT);
			assertThat(e.getMessage(), containsString(expectedMessage));
			assertThat(e.getMessage(), containsString("a reason the class is mutable"));
		}
	}
	
	@Test public void expectedIsImmutableStatusDoesNotThrowException() throws Exception {
		AnalysisResult analysisResult = new AnalysisResult("g.h.i", IsImmutable.MAYBE, unusedReasons());
		reporter.expectedIsImmutable(IsImmutable.MAYBE, analysisResult);
	}
	
	@SuppressWarnings("unchecked") @Test public void allowedReasonDoesNotThrowException() {
		Matcher<AnalysisResult> allowed = mock(Matcher.class);
		AnalysisResult result = new AnalysisResult("j.k.l", DEFINITELY_NOT, unusedReasons());
		
		when(allowed.matches(result)).thenReturn(true);
		
		reporter.expectedIsImmutable(DEFINITELY, result, allowed);
	}

	private Collection<CheckerReasonDetail> unusedReasons() {
		return asList(new CheckerReasonDetail("this reason is not meant to be involved", null, PUBLISHED_NON_FINAL_FIELD));
	}
}
