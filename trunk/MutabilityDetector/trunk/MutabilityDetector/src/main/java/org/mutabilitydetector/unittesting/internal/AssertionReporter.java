/*
 *    Copyright (c) 2008-2011 Graham Allan
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.mutabilitydetector.unittesting.internal;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.unittesting.MutabilityAssertionError;

/**
 * {@link AssertionReporter} is responsible for making an assertion in a test fail, by preparing and throwing the
 * appropriate {@link MutabilityAssertionError}.
 */
public class AssertionReporter {


    public void assertThat(AnalysisResult analysisResult, Matcher<AnalysisResult> areImmutable) {
        try {
            MatcherAssert.assertThat(analysisResult, areImmutable);
        } catch (AssertionError e) {
            throw new MutabilityAssertionError(e.getMessage());
        }
    }
}
