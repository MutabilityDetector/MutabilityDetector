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



import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mutabilitydetector.MutabilityReason.ABSTRACT_TYPE_INHERENTLY_MUTABLE;
import static org.mutabilitydetector.MutabilityReason.NON_FINAL_FIELD;
import static org.mutabilitydetector.MutableReasonDetail.newMutableReasonDetail;
import static org.mutabilitydetector.TestUtil.unusedCodeLocation;

import org.junit.Test;
import org.mutabilitydetector.MutableReasonDetail;

public class AllowingNonFinalFieldsTest {

    @Test
    public void matchesAReasonOfHavingNonFinalField() throws Exception {
        MutableReasonDetail reason = newMutableReasonDetail("any message", unusedCodeLocation(), NON_FINAL_FIELD);
        
        assertThat(new AllowingNonFinalFields().matches(reason), is(true));
    }
    
    @Test
    public void doesNotMatchAnyOtherReason() throws Exception {
        MutableReasonDetail reason = newMutableReasonDetail("any message", unusedCodeLocation(), ABSTRACT_TYPE_INHERENTLY_MUTABLE);
        
        assertThat(new AllowingNonFinalFields().matches(reason), is(false));
    }
    
}
