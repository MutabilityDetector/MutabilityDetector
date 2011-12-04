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
package org.mutabilitydetector.unittesting.matchers.reasons;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mutabilitydetector.MutabilityReason.ABSTRACT_TYPE_INHERENTLY_MUTABLE;
import static org.mutabilitydetector.MutabilityReason.CAN_BE_SUBCLASSED;
import static org.mutabilitydetector.MutabilityReason.FIELD_CAN_BE_REASSIGNED;
import static org.mutabilitydetector.MutabilityReason.NOT_DECLARED_FINAL;
import static org.mutabilitydetector.MutableReasonDetail.newMutableReasonDetail;

import org.junit.Test;
import org.mutabilitydetector.TestUtil;
import org.mutabilitydetector.locations.CodeLocation;

public class AllowingForSubclassingTest {

    private final CodeLocation<?> unusedCodeLocation = TestUtil.unusedCodeLocation();
    private final String unusedClassName = "";

    @SuppressWarnings("deprecation")
    @Test
    public void matchesCheckerReasonDetailWithReasonOfNotDeclaredFinal() throws Exception {
        AllowingForSubclassing matcher = new AllowingForSubclassing();
        assertTrue(matcher.matches(newMutableReasonDetail(unusedClassName, unusedCodeLocation, NOT_DECLARED_FINAL)));
    }

    @Test
    public void matchesCheckerReasonDetailWithReasonOfBeingSubclassable() throws Exception {
        AllowingForSubclassing matcher = new AllowingForSubclassing();
        assertTrue(matcher.matches(newMutableReasonDetail(unusedClassName, unusedCodeLocation, CAN_BE_SUBCLASSED)));
    }
    
    @Test
    public void matchesCheckerReasonDetailWithReasonOfBeingDeclaredAsAbstractType() throws Exception {
        AllowingForSubclassing matcher = new AllowingForSubclassing();
        assertTrue(matcher.matches(newMutableReasonDetail(unusedClassName, unusedCodeLocation, ABSTRACT_TYPE_INHERENTLY_MUTABLE)));
    }

    @Test
    public void doesNotMatchWhenOnlyReasonIsSomethingUnrelatedToSubclassing() throws Exception {
        AllowingForSubclassing matcher = new AllowingForSubclassing();
        assertFalse(matcher.matches(newMutableReasonDetail(unusedClassName, unusedCodeLocation, FIELD_CAN_BE_REASSIGNED)));
    }

}
