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



import static java.lang.String.format;

import org.hamcrest.Description;
import org.hamcrest.StringDescription;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.unittesting.MutabilityAssertionError;
import org.mutabilitydetector.unittesting.matchers.reasons.WithAllowedReasonsMatcher;

/**
 * {@link AssertionReporter} is responsible for making an assertion in a test fail, by preparing and throwing the
 * appropriate {@link MutabilityAssertionError}.
 */
public final class AssertionReporter {


    public void assertThat(AnalysisResult analysisResult, WithAllowedReasonsMatcher resultMatcher) {
        if (!resultMatcher.matches(analysisResult)) {
            Description description = new StringDescription();
            description.appendText(format("%nExpected: "))
                       .appendDescriptionOf(resultMatcher)
                       .appendText(format("%n     but: "));
            resultMatcher.describeMismatch(analysisResult, description);
            
            throw new MutabilityAssertionError(description.toString());
        }
    }
}
