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

package org.mutabilitydetector.unittesting;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.mutabilitydetector.AnalysisResult.analysisResult;
import static org.mutabilitydetector.IsImmutable.NOT_IMMUTABLE;
import static org.mutabilitydetector.MutabilityReason.ABSTRACT_TYPE_TO_FIELD;
import static org.mutabilitydetector.MutabilityReason.FIELD_CAN_BE_REASSIGNED;
import static org.mutabilitydetector.MutableReasonDetail.newMutableReasonDetail;
import static org.mutabilitydetector.TestUtil.unusedMutableReasonDetail;
import static org.mutabilitydetector.locations.ClassLocation.fromInternalName;
import static org.mutabilitydetector.unittesting.AllowedReason.noReasonsAllowed;
import static org.mutabilitydetector.unittesting.AllowedReason.provided;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.benchmarks.types.InterfaceType;
import org.mutabilitydetector.unittesting.matchers.reasons.AllowingNonFinalFields;

public class AllowedReasonTest {

    @Test
    public void providedClassIsAlsoImmutableAllowsAssigningAbstractType() throws Exception {
        MutableReasonDetail reason = newMutableReasonDetail("assigning abstract type (a.b.c)",
                fromInternalName("a/b/c"),
                ABSTRACT_TYPE_TO_FIELD);

        Matcher<MutableReasonDetail> allowed = provided("a.b.c").isAlsoImmutable();
        assertThat(allowed.matches(reason), is(true));
    }

    @Test
    public void doesNotMatchWhenReasonIsNotAllowed() throws Exception {
        MutableReasonDetail reason = newMutableReasonDetail("has setter method",
                fromInternalName("a/b/c"),
                FIELD_CAN_BE_REASSIGNED);

        Matcher<MutableReasonDetail> allowed = AllowedReason.provided("a.b.c").isAlsoImmutable();
        assertThat(allowed.matches(reason), is(false));
    }

    @Test
    public void providedMethodCanBeCalledWithClassObject() throws Exception {
        String message = format("assigning abstract type (%s) to field.", InterfaceType.class.getName());
        MutableReasonDetail reason = newMutableReasonDetail(message, fromInternalName("a/b/c"), ABSTRACT_TYPE_TO_FIELD);
        Matcher<MutableReasonDetail> allowed = AllowedReason.provided(InterfaceType.class).isAlsoImmutable();

        assertThat(allowed.matches(reason), is(true));
    }

    @Test
    public void noneAllowedMatcherReturnsFalseForAnyReason() throws Exception {
        AnalysisResult result = analysisResult("any", NOT_IMMUTABLE, unusedMutableReasonDetail());
        Matcher<MutableReasonDetail> nonAllowed = noReasonsAllowed();

        assertThat(nonAllowed.matches(result), is(false));
    }
    
    @Test
    public void allowingNonFinalFieldsReturnsMatcherForNonFinalFieldsReason() throws Exception {
        Matcher<MutableReasonDetail> allowingNonFinalFields = AllowedReason.allowingNonFinalFields();
        
        assertThat(allowingNonFinalFields, instanceOf(AllowingNonFinalFields.class));
    }

}
