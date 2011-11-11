/*
 * Mutability Detector
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * 
 * Further licensing information for this project can be found in
 * license/LICENSE.txt
 */

package org.mutabilitydetector.unittesting.internal;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.fail;
import static org.mutabilitydetector.AnalysisResult.analysisResult;
import static org.mutabilitydetector.IAnalysisSession.IsImmutable.IMMUTABLE;
import static org.mutabilitydetector.IAnalysisSession.IsImmutable.NOT_IMMUTABLE;
import static org.mutabilitydetector.MutabilityReason.ESCAPED_THIS_REFERENCE;
import static org.mutabilitydetector.MutabilityReason.PUBLISHED_NON_FINAL_FIELD;
import static org.mutabilitydetector.locations.ClassLocation.fromDotted;
import static org.mutabilitydetector.locations.Dotted.dotted;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;
import static org.mutabilitydetector.unittesting.matchers.IsImmutableMatcher.hasIsImmutableStatusOf;

import java.util.Collection;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.CheckerReasonDetail;
import org.mutabilitydetector.IAnalysisSession.IsImmutable;
import org.mutabilitydetector.TestUtil;
import org.mutabilitydetector.unittesting.MutabilityAssertionError;
import org.mutabilitydetector.unittesting.MutabilityMatchers;

public class AssertionReporterTest {

    private AssertionReporter reporter;

    @Before
    public void setUp() {
        reporter = new AssertionReporter();
    }

    @Test
    public void reporterDoesNotThrowAssertionErrorForImmutableResult() throws Exception {
        AnalysisResult analysisResult = AnalysisResult.definitelyImmutable("a.b.c");
        reporter.assertThat(analysisResult, areImmutable());
    }

    @Test(expected = MutabilityAssertionError.class)
    public void reporterThrowsExceptionForMutableResult() {
        AnalysisResult analysisResult = analysisResult("a.b.c", NOT_IMMUTABLE, unusedReasons());
        reporter.assertThat(analysisResult, areImmutable());
    }

    @Test
    public void thrownExceptionContainsHelpfulMessage() throws Exception {
        CheckerReasonDetail reason = new CheckerReasonDetail("a reason the class is mutable",
                null,
                PUBLISHED_NON_FINAL_FIELD);

        AnalysisResult analysisResult = analysisResult("d.e.SimpleClassName", NOT_IMMUTABLE, asList(reason));
        try {
            reporter.assertThat(analysisResult, areImmutable());
            fail("expected exception");
        } catch (MutabilityAssertionError e) {
            assertThat(e.getMessage(), containsString("Expected: d.e.SimpleClassName to be " + IMMUTABLE + "\n"));
            assertThat(e.getMessage(), containsString("but: d.e.SimpleClassName is actually " + NOT_IMMUTABLE + "\n"));
            assertThat(e.getMessage(), containsString("Reasons:"));
            assertThat(e.getMessage(), containsString("a reason the class is mutable"));
        }
    }

    @Test
    public void expectedIsImmutableStatusDoesNotThrowException() throws Exception {
        AnalysisResult analysisResult = analysisResult("g.h.i", IsImmutable.EFFECTIVELY_IMMUTABLE, unusedReasons());
        reporter.assertThat(analysisResult, hasIsImmutableStatusOf(IsImmutable.EFFECTIVELY_IMMUTABLE));
    }

    @Ignore("In progress")
    @Test
    public void thrownExceptionContainsMessageAboutWarningsWhichAreSuppressed() throws Exception {
        CheckerReasonDetail reason = new CheckerReasonDetail("a reason the class is mutable",
                null,
                PUBLISHED_NON_FINAL_FIELD);

        AnalysisResult analysisResult = analysisResult("d.e.SimpleClassName", NOT_IMMUTABLE, asList(reason));
        try {
            reporter.assertThat(analysisResult, areImmutable());
            fail("expected exception");
        } catch (MutabilityAssertionError e) {
            String expectedMessage = format("\nSuppressed reasons:" + "\n\tNo reasons have been suppressed.");
            assertThat(e.getMessage(), containsString(expectedMessage));
        }
    }
    
    @Test(expected=MutabilityAssertionError.class)
    public void performsAssertThatButWrapsExceptionInMutabilityAssertionError() throws Exception {
        reporter.assertThat(analysisResult("a.b.c", IsImmutable.NOT_IMMUTABLE, unusedReasons()), MutabilityMatchers.areImmutable());
    }

    @Test
    public void performsAssertThatButWrapsExceptionInMutabilityAssertionErrorWithSameMessage() throws Exception {
        CheckerReasonDetail reasonDetail = new CheckerReasonDetail("this message should appear", 
                                                                   fromDotted(dotted("a.b.c")), 
                                                                   ESCAPED_THIS_REFERENCE);
        try {
            reporter.assertThat(analysisResult("a.b.c", IsImmutable.NOT_IMMUTABLE, reasonDetail), areImmutable());
            fail("expected exception");
        } catch (MutabilityAssertionError expectedError) {
            assertThat(expectedError.getMessage(), allOf(containsString("a.b.c to be IMMUTABLE\n"),
                                                         containsString("a.b.c is actually NOT_IMMUTABLE\n"),
                                                         containsString("this message should appear")));
        }
    }

    private static Collection<CheckerReasonDetail> unusedReasons() {
        return TestUtil.unusedCheckerReasonDetails();
    }
}
