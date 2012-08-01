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

package org.mutabilitydetector;

import static org.mutabilitydetector.unittesting.AllowedReason.provided;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;

import java.util.ArrayList;

import org.junit.Test;

public class AnalysisResultTest {

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionWhenNotImmutableAndNoReasonsGiven_varargs() throws Exception {
        AnalysisResult.analysisResult("someclass", IsImmutable.NOT_IMMUTABLE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionWhenNotImmutableAndNoReasonsGiven_collection() throws Exception {
        AnalysisResult.analysisResult("someclass", IsImmutable.NOT_IMMUTABLE, new ArrayList<MutableReasonDetail>());
    }

    @Test
    public void doesntThrowExceptionWhenImmutableAndNoReasonGiven() throws Exception {
        AnalysisResult.analysisResult("someclass", IsImmutable.IMMUTABLE);
    }
    
    @Test
    public void isImmutable() throws Exception {
        assertInstancesOf(AnalysisResult.class, areImmutable(), provided(String.class).isAlsoImmutable());
    }

}
