/*
 * Mutability Detector
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * 
 * Further licensing information for this project can be found in
 * license/LICENSE.txt
 */

package org.mutabilitydetector.unittesting;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mutabilitydetector.IAnalysisSession.IsImmutable.IMMUTABLE;
import static org.mutabilitydetector.IAnalysisSession.IsImmutable.NOT_IMMUTABLE;
import static org.mutabilitydetector.MutabilityReason.PUBLISHED_NON_FINAL_FIELD;

import java.util.Collection;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.CheckerReasonDetail;
import org.mutabilitydetector.IAnalysisSession.IsImmutable;
import org.mutabilitydetector.TestUtil;
import org.mutabilitydetector.unittesting.matchers.AnalysisResultMatcher;

public class AssertionReporterTest {

    private AssertionReporter reporter;

    @Before
    public void setUp() {
        reporter = new AssertionReporter();
    }

    @Test
    public void reporterDoesNotThrowAssertionErrorForImmutableResult() throws Exception {
        AnalysisResult analysisResult = AnalysisResult.definitelyImmutable("a.b.c");
        reporter.expectedImmutable(analysisResult);
    }

    @Test(expected = MutabilityAssertionError.class)
    public void reporterThrowsExceptionForMutableResult() {
        AnalysisResult analysisResult = new AnalysisResult("a.b.c", NOT_IMMUTABLE, unusedReasons());
        reporter.expectedImmutable(analysisResult);
    }

    @Test
    public void thrownExceptionContainsHelpfulMessage() throws Exception {
        CheckerReasonDetail reason = new CheckerReasonDetail("a reason the class is mutable",
                null,
                PUBLISHED_NON_FINAL_FIELD);

        AnalysisResult analysisResult = new AnalysisResult("d.e.SimpleClassName", NOT_IMMUTABLE, asList(reason));
        try {
            reporter.expectedImmutable(analysisResult);
            fail("expected exception");
        } catch (MutabilityAssertionError e) {
            String expectedMessage = format("\nExpected class [%s] to be [%s] immutable," + "\nbut was [%s] immutable.",
                    "SimpleClassName",
                    IMMUTABLE,
                    NOT_IMMUTABLE);
            assertThat(e.getMessage(), containsString(expectedMessage));
            assertThat(e.getMessage(), containsString("a reason the class is mutable"));
        }
    }

    @Test
    public void expectedIsImmutableStatusDoesNotThrowException() throws Exception {
        AnalysisResult analysisResult = new AnalysisResult("g.h.i", IsImmutable.EFFECTIVELY_IMMUTABLE, unusedReasons());
        reporter.expectedIsImmutable(IsImmutable.EFFECTIVELY_IMMUTABLE, analysisResult);
    }

    @Test
    public void allowedReasonDoesNotThrowException() {
        AnalysisResultMatcher allowed = mock(AnalysisResultMatcher.class);
        AnalysisResult result = new AnalysisResult("j.k.l", NOT_IMMUTABLE, unusedReasons());

        when(allowed.matches(result)).thenReturn(true);

        reporter.expectedIsImmutable(IMMUTABLE, result, allowed);
    }

    @Ignore("In progress")
    @Test
    public void thrownExceptionContainsMessageAboutWarningsWhichAreSuppressed() throws Exception {
        CheckerReasonDetail reason = new CheckerReasonDetail("a reason the class is mutable",
                null,
                PUBLISHED_NON_FINAL_FIELD);

        AnalysisResult analysisResult = new AnalysisResult("d.e.SimpleClassName", NOT_IMMUTABLE, asList(reason));
        try {
            reporter.expectedImmutable(analysisResult);
            fail("expected exception");
        } catch (MutabilityAssertionError e) {
            String expectedMessage = format("\nSuppressed reasons:" + "\n\tNo reasons have been suppressed.");
            assertThat(e.getMessage(), containsString(expectedMessage));
        }
    }

    private static Collection<CheckerReasonDetail> unusedReasons() {
        return TestUtil.unusedCheckerReasonDetails();
    }
}
