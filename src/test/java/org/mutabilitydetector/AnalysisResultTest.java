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

import static org.junit.Assert.assertEquals;
import static org.mutabilitydetector.AnalysisResult.analysisResult;
import static org.mutabilitydetector.MutableReasonDetail.newMutableReasonDetail;
import static org.mutabilitydetector.locations.Dotted.dotted;
import static org.mutabilitydetector.unittesting.AllowedReason.provided;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;

import java.util.ArrayList;

import org.junit.Test;
import org.mutabilitydetector.locations.ClassLocation;

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
        assertInstancesOf(AnalysisResult.class, areImmutable(), 
                provided(String.class, MutableReasonDetail.class).isAlsoImmutable());
    }
    
    @Test
    public void equalityIsBasedOnClassNameResultAndReasons() throws Exception {
        MutableReasonDetail reason = newMutableReasonDetail("my mutability reason", 
                                                            ClassLocation.from(dotted("some.Class")), 
                                                            MutabilityReason.CAN_BE_SUBCLASSED);
        
        AnalysisResult first = analysisResult("some.Class", IsImmutable.NOT_IMMUTABLE, reason);
        AnalysisResult second = analysisResult("some.Class", IsImmutable.NOT_IMMUTABLE, reason);

        assertEquals(first, second);
    }

    @Test
    public void definitelyImmutableResultsAreEqual() throws Exception {
        AnalysisResult first = AnalysisResult.definitelyImmutable("some.Class");
        AnalysisResult second = AnalysisResult.definitelyImmutable("some.Class");
        
        assertEquals(first, second);
    }


}
