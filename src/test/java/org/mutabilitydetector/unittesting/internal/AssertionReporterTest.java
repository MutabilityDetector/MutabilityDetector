package org.mutabilitydetector.unittesting.internal;

/*
 * #%L
 * MutabilityDetector
 * %%
 * Copyright (C) 2008 - 2014 Graham Allan
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */



import static java.lang.System.getProperty;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;
import static org.mutabilitydetector.AnalysisResult.analysisResult;
import static org.mutabilitydetector.IsImmutable.IMMUTABLE;
import static org.mutabilitydetector.IsImmutable.NOT_IMMUTABLE;
import static org.mutabilitydetector.MutabilityReason.ESCAPED_THIS_REFERENCE;
import static org.mutabilitydetector.MutabilityReason.PUBLISHED_NON_FINAL_FIELD;
import static org.mutabilitydetector.MutableReasonDetail.newMutableReasonDetail;
import static org.mutabilitydetector.locations.ClassLocation.from;
import static org.mutabilitydetector.locations.Dotted.dotted;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;
import static org.mutabilitydetector.unittesting.matchers.IsImmutableMatcher.hasIsImmutableStatusOf;
import static org.mutabilitydetector.unittesting.matchers.reasons.WithAllowedReasonsMatcher.withNoAllowedReasons;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.TestUtil;
import org.mutabilitydetector.locations.ClassLocation;
import org.mutabilitydetector.locations.CodeLocation;
import org.mutabilitydetector.unittesting.MutabilityAssertionError;

public class AssertionReporterTest {

    private AssertionReporter reporter;
    private final String newline = getProperty("line.separator");

    @Before
    public void setUp() {
        reporter = new AssertionReporter();
    }

    @Test
    public void reporterDoesNotThrowAssertionErrorForImmutableResult() throws Exception {
        AnalysisResult analysisResult = AnalysisResult.definitelyImmutable("a.b.c");
        reporter.assertThat(analysisResult, withNoAllowedReasons(areImmutable()));
    }

    @Test(expected = MutabilityAssertionError.class)
    public void reporterThrowsExceptionForMutableResult() {
        AnalysisResult analysisResult = analysisResult("a.b.c", NOT_IMMUTABLE, unusedReasons());
        reporter.assertThat(analysisResult, withNoAllowedReasons(areImmutable()));
    }

    @Test
    public void thrownExceptionContainsHelpfulMessage() throws Exception {
        CodeLocation<ClassLocation> codeLocation = ClassLocation.from(dotted("d.e.SimpleClassName"));
        MutableReasonDetail reason = newMutableReasonDetail("a reason the class is mutable",
                codeLocation,
                PUBLISHED_NON_FINAL_FIELD);

        AnalysisResult analysisResult = analysisResult("d.e.SimpleClassName", NOT_IMMUTABLE, asList(reason));
        try {
            reporter.assertThat(analysisResult, withNoAllowedReasons(areImmutable()));
            fail("expected exception");
        } catch (MutabilityAssertionError e) {
            String[] errorMessageLines = e.getMessage().split(newline);
            assertThat(errorMessageLines[0], is(""));
            assertThat(errorMessageLines[1], is("Expected: d.e.SimpleClassName to be " + IMMUTABLE));
            assertThat(errorMessageLines[2], is("     but: d.e.SimpleClassName is actually " + NOT_IMMUTABLE));
            assertThat(errorMessageLines[3], is("    Reasons:"));
            assertThat(errorMessageLines[4], is("        a reason the class is mutable [Class: d.e.SimpleClassName]" ));
            assertThat(errorMessageLines[5], is("    Allowed reasons:" ));
            assertThat(errorMessageLines[6], is("        None." ));
        }
    }

    @Test
    public void expectedIsImmutableStatusDoesNotThrowException() throws Exception {
        AnalysisResult analysisResult = analysisResult("g.h.i", IsImmutable.EFFECTIVELY_IMMUTABLE, unusedReasons());
        reporter.assertThat(analysisResult, withNoAllowedReasons(hasIsImmutableStatusOf(IsImmutable.EFFECTIVELY_IMMUTABLE)));
    }

    @Test
    public void performsAssertThatButWrapsExceptionInMutabilityAssertionErrorWithSameMessage() throws Exception {
        MutableReasonDetail reasonDetail = newMutableReasonDetail("this message should appear", 
                                                                   from(dotted("a.b.c")), 
                                                                   ESCAPED_THIS_REFERENCE);
        try {
            reporter.assertThat(analysisResult("a.b.c", IsImmutable.NOT_IMMUTABLE, reasonDetail), withNoAllowedReasons(areImmutable()));
            fail("expected exception");
        } catch (MutabilityAssertionError expectedError) {
            assertThat(expectedError.getMessage(), allOf(containsString("a.b.c to be IMMUTABLE" + newline),
                                                         containsString("a.b.c is actually NOT_IMMUTABLE" + newline),
                                                         containsString("this message should appear")));
        }
    }

    private static Collection<MutableReasonDetail> unusedReasons() {
        return TestUtil.unusedMutableReasonDetails();
    }
}
