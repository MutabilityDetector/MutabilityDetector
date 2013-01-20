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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.mutabilitydetector.MutabilityReason.ABSTRACT_TYPE_TO_FIELD;
import static org.mutabilitydetector.MutabilityReason.ARRAY_TYPE_INHERENTLY_MUTABLE;
import static org.mutabilitydetector.MutabilityReason.FIELD_CAN_BE_REASSIGNED;
import static org.mutabilitydetector.MutableReasonDetail.newMutableReasonDetail;
import static org.mutabilitydetector.locations.ClassLocation.fromInternalName;
import static org.mutabilitydetector.locations.FieldLocation.fieldLocation;
import static org.mutabilitydetector.unittesting.AllowedReason.assumingFields;
import static org.mutabilitydetector.unittesting.AllowedReason.provided;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.benchmarks.types.InterfaceType;
import org.mutabilitydetector.unittesting.matchers.reasons.AllowingNonFinalFields;
import org.mutabilitydetector.unittesting.matchers.reasons.NoReasonsAllowed;

public class AllowedReasonTest {

    @Test
    public void providedClassIsAlsoImmutableAllowsAssigningAbstractType() throws Exception {
        MutableReasonDetail reason = newMutableReasonDetail("assigning abstract type (a.b.c)",
                fromInternalName("a/b/c"),
                ABSTRACT_TYPE_TO_FIELD);

        assertThat(provided("a.b.c").isAlsoImmutable(), allows(reason));
    }

    @Test
    public void doesNotMatchWhenReasonIsNotAllowed() throws Exception {
        MutableReasonDetail reason = newMutableReasonDetail("has setter method",
                fromInternalName("a/b/c"),
                FIELD_CAN_BE_REASSIGNED);

        assertThat(provided("a.b.c").isAlsoImmutable(), not(allows(reason)));
    }

    @Test
    public void providedMethodCanBeCalledWithClassObject() throws Exception {
        String message = format("assigning abstract type (%s) to field.", InterfaceType.class.getName());
        MutableReasonDetail reason = newMutableReasonDetail(message, fromInternalName("a/b/c"), ABSTRACT_TYPE_TO_FIELD);
        
        assertThat(provided(InterfaceType.class).isAlsoImmutable(), allows(reason));
    }

    @Test
    public void noneAllowedMatcherReturnsFalseForAnyReason() throws Exception {
        MutableReasonDetail reason = newMutableReasonDetail("assigning abstract type (a.b.c)",
                                                            fromInternalName("a/b/c"),
                                                            ABSTRACT_TYPE_TO_FIELD);
        assertThat(NoReasonsAllowed.noReasonsAllowed(), not(allows(reason)));
    }
    
    @Test
    public void allowingNonFinalFieldsReturnsMatcherForNonFinalFieldsReason() throws Exception {
        assertThat(AllowedReason.allowingNonFinalFields(), instanceOf(AllowingNonFinalFields.class));
    }

    @Test
    public void canAllowArrayFields() throws Exception {
        MutableReasonDetail reason = newMutableReasonDetail("has array field",
                                                            fieldLocation("myArrayField", fromInternalName("a/b/c")),
                                                            ARRAY_TYPE_INHERENTLY_MUTABLE);
        
        assertThat(assumingFields("myArrayField").areNotModifiedAndDoNotEscape(),
                   allows(reason));

    }
    
    private static Matcher<Matcher<MutableReasonDetail>> allows(final MutableReasonDetail reason) {
        return new TypeSafeMatcher<Matcher<MutableReasonDetail>>() {

            @Override public void describeTo(Description description) { }

            @Override
            protected boolean matchesSafely(Matcher<MutableReasonDetail> item) {
                return item.matches(reason);
            }
            
        };
    }
}
