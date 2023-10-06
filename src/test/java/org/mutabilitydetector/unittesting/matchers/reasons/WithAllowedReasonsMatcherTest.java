package org.mutabilitydetector.unittesting.matchers.reasons;

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


import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.StringDescription;
import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.TestUtil;
import org.mutabilitydetector.locations.CodeLocation;
import org.mutabilitydetector.unittesting.matchers.IsImmutableMatcher;

import java.util.Collections;

import static java.lang.String.format;
import static java.lang.System.getProperty;
import static java.util.Collections.singleton;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mutabilitydetector.IsImmutable.IMMUTABLE;
import static org.mutabilitydetector.IsImmutable.NOT_IMMUTABLE;
import static org.mutabilitydetector.MutabilityReason.CAN_BE_SUBCLASSED;
import static org.mutabilitydetector.MutableReasonDetail.newMutableReasonDetail;
import static org.mutabilitydetector.AnalysisResult.analysisResult;
import static org.mutabilitydetector.AnalysisResult.definitelyImmutable;
import static org.mutabilitydetector.TestUtil.unusedMutableReasonDetail;
import static org.mutabilitydetector.TestUtil.unusedReason;
import static org.mutabilitydetector.locations.CodeLocation.ClassLocation.from;
import static org.mutabilitydetector.locations.CodeLocation.ClassLocation.fromInternalName;
import static org.mutabilitydetector.locations.Dotted.dotted;
import static org.mutabilitydetector.unittesting.matchers.reasons.NoReasonsAllowed.noReasonsAllowed;
import static org.mutabilitydetector.unittesting.matchers.reasons.WithAllowedReasonsMatcher.withAllowedReasons;

@SuppressWarnings("unchecked")
public class WithAllowedReasonsMatcherTest {

    CodeLocation<?> unusedCodeLocation = TestUtil.unusedCodeLocation();

    Matcher<MutableReasonDetail> noReasonsAllowed = noReasonsAllowed();
    
    @Test
    public void passesWhenPrimaryResultPasses() throws Exception {
        IsImmutableMatcher isImmutable = IsImmutableMatcher.hasIsImmutableStatusOf(IMMUTABLE);
        AnalysisResult analysisResult = definitelyImmutable("some.class");

        WithAllowedReasonsMatcher withReasonsMatcher = withAllowedReasons(isImmutable, singleton(noReasonsAllowed));

        assertThat(withReasonsMatcher.matches(analysisResult), is(true));
    }

    @Test
    public void failsWhenPrimaryResultFailsAndNoReasonsAreAllowed() throws Exception {
        IsImmutableMatcher isImmutable = IsImmutableMatcher.hasIsImmutableStatusOf(NOT_IMMUTABLE);
        AnalysisResult analysisResult = definitelyImmutable("some class");
        
        WithAllowedReasonsMatcher withReasonsMatcher = withAllowedReasons(isImmutable, 
                Collections.<Matcher<MutableReasonDetail>>emptyList());
        
        assertThat(withReasonsMatcher.matches(analysisResult), is(false));
    }

    @Test
    public void failsWhenExpectingNotImmutableAndRealResultIsImmutableWithNoReasons() throws Exception {
        IsImmutableMatcher isImmutable = IsImmutableMatcher.hasIsImmutableStatusOf(IMMUTABLE);
        AnalysisResult analysisResult = analysisResult("some class", NOT_IMMUTABLE, unusedMutableReasonDetail());
        
        WithAllowedReasonsMatcher withReasonsMatcher = withAllowedReasons(isImmutable, singleton(noReasonsAllowed));
        
        assertThat(withReasonsMatcher.matches(analysisResult), is(false));
    }
    
    @Test public void passesWhenResultDoesNotMatchButTheOffendingReasonsAreAllowed() {
        MutableReasonDetail anyReason = unusedMutableReasonDetail();
        
        Matcher<MutableReasonDetail> allowWhateverReason = mock(Matcher.class);
        when(allowWhateverReason.matches(anyReason)).thenReturn(true);
        
        IsImmutableMatcher isImmutable = IsImmutableMatcher.hasIsImmutableStatusOf(IMMUTABLE);
        AnalysisResult analysisResult = analysisResult("some class", NOT_IMMUTABLE, anyReason);
        
        WithAllowedReasonsMatcher withReasonsMatcher = withAllowedReasons(isImmutable, singleton(allowWhateverReason));
        
        assertThat(withReasonsMatcher.matches(analysisResult), is(true));
    }
    
    @Test public void failsWhenResultDoesNotMatchAndOnlyOneOfManyReasonsAreAllowed() {
        MutableReasonDetail allowedReason = newMutableReasonDetail("allowed", unusedCodeLocation, unusedReason());
        MutableReasonDetail disallowedReason = newMutableReasonDetail("disallowed", unusedCodeLocation, unusedReason());
        
        Matcher<MutableReasonDetail> onlyAllowOneReason = mock(Matcher.class);
        when(onlyAllowOneReason.matches(allowedReason)).thenReturn(true);
        when(onlyAllowOneReason.matches(disallowedReason)).thenReturn(false);
        
        IsImmutableMatcher isImmutable = IsImmutableMatcher.hasIsImmutableStatusOf(IMMUTABLE);
        AnalysisResult analysisResult = analysisResult("some class", NOT_IMMUTABLE, allowedReason, disallowedReason);
        
        WithAllowedReasonsMatcher withReasonsMatcher = withAllowedReasons(isImmutable, singleton(onlyAllowOneReason));
        
        assertThat(withReasonsMatcher.matches(analysisResult), is(false));
    }
    
    @Test
    public void mismatchDescriptionListsWhichReasonsHaveBeenAllowed() throws Exception {
        MutableReasonDetail allowedReason = newMutableReasonDetail("This reason has been Allowed.", from(dotted("some.class")), unusedReason());
        MutableReasonDetail disallowedReason = newMutableReasonDetail("disallowed", unusedCodeLocation, unusedReason());
        
        Matcher<MutableReasonDetail> onlyAllowOneReason = mock(Matcher.class);
        when(onlyAllowOneReason.matches(allowedReason)).thenReturn(true);
        when(onlyAllowOneReason.matches(disallowedReason)).thenReturn(false);
        
        IsImmutableMatcher isImmutable = IsImmutableMatcher.hasIsImmutableStatusOf(IMMUTABLE);
        AnalysisResult analysisResult = analysisResult("some class", NOT_IMMUTABLE, allowedReason, disallowedReason);
        
        WithAllowedReasonsMatcher withReasonsMatcher = withAllowedReasons(isImmutable, singleton(onlyAllowOneReason));
        
        try {
            MatcherAssert.assertThat(analysisResult, withReasonsMatcher);
            fail("Expected assertion to fail");
        } catch(AssertionError expectedError) {
            assertThat(expectedError.getMessage(), 
                       allOf(containsString(format("    Allowed reasons:%n")),
                             containsString(format("        %s %s%n", allowedReason.message(), allowedReason.codeLocation().prettyPrint()))));
        }
    }
    
    @Test
    public void mismatchDescriptionExplicitlyStatesNoReasonsHaveBeenAllowed() throws Exception {
        MutableReasonDetail disallowedReason = newMutableReasonDetail("disallowed", unusedCodeLocation, unusedReason());
        
        Matcher<MutableReasonDetail> onlyAllowOneReason = mock(Matcher.class);
        IsImmutableMatcher isImmutable = IsImmutableMatcher.hasIsImmutableStatusOf(IMMUTABLE);
        AnalysisResult analysisResult = analysisResult("some class", NOT_IMMUTABLE, disallowedReason);
        
        WithAllowedReasonsMatcher withReasonsMatcher = withAllowedReasons(isImmutable, singleton(noReasonsAllowed));
        
        try {
            MatcherAssert.assertThat(analysisResult, withReasonsMatcher);
            fail("Expected assertion to fail");
        } catch(AssertionError expectedError) {
            String[] errorMessageLines = errorMessageFrom(expectedError).split(getProperty("line.separator"));
            
            assertThat(errorMessageLines[5], is("    Allowed reasons:"));
            assertThat(errorMessageLines[6], is("        None."));
        }
    }

    private String errorMessageFrom(AssertionError expectedError) {
        String matcherAssertMessageWithHardcodedUnixNewLines = expectedError.getMessage();
        return matcherAssertMessageWithHardcodedUnixNewLines
                .replace("\nExpected:", format("%nExpected:"))
                .replace("\n     but:", format("%n     but:"));
    }
    
    @Test
    public void describesMismatchItselfIfNoSuchMethodExistsForDelegateMatcher() throws Exception {
        WithAllowedReasonsMatcher usingHamcrest1_1_matcher = withAllowedReasons(new Hamcrest1_1_Matcher(), singleton(noReasonsAllowed));
        
        Description description = new StringDescription();
        AnalysisResult result = analysisResult("org.some.Thing", NOT_IMMUTABLE, 
                                               newMutableReasonDetail("it sucks", fromInternalName("org/some/Thing"), CAN_BE_SUBCLASSED));
        usingHamcrest1_1_matcher.describeMismatch(result, description);
        
        String expectedError = String.format(
                "org.some.Thing is actually NOT_IMMUTABLE%n" + 
                "    Reasons:%n" + 
                "        it sucks [at org.some.Thing(Thing.java:1)]%n" +
                "    Allowed reasons:%n" + 
                "        None.");
        
        assertThat(description.toString(), is(expectedError));
        
    }
    
    private static class Hamcrest1_1_Matcher extends BaseMatcher<AnalysisResult> {
        @Override
        public boolean matches(Object item) {
            return false;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("either")
                       .appendValue(IsImmutable.EFFECTIVELY_IMMUTABLE)
                       .appendText(" or ")
                       .appendValue(IsImmutable.IMMUTABLE);
        }
        
        @Override
        public void describeMismatch(Object item, Description description) {
            throw new NoSuchMethodError();
        }
        
    }

}
