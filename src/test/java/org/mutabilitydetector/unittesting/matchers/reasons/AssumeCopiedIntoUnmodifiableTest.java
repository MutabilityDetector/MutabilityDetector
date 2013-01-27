/*
 *    Copyright (c) 2008-2013 Graham Allan
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

import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mutabilitydetector.MutabilityReason.ABSTRACT_COLLECTION_TYPE_TO_FIELD;
import static org.mutabilitydetector.MutabilityReason.ABSTRACT_TYPE_TO_FIELD;
import static org.mutabilitydetector.MutabilityReason.COLLECTION_FIELD_WITH_MUTABLE_ELEMENT_TYPE;
import static org.mutabilitydetector.MutableReasonDetail.newMutableReasonDetail;
import static org.mutabilitydetector.locations.ClassLocation.from;
import static org.mutabilitydetector.locations.FieldLocation.fieldLocation;
import static org.mutabilitydetector.unittesting.matchers.reasons.AssumeCopiedIntoUnmodifiable.assuming;

import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.locations.Dotted;
import org.mutabilitydetector.locations.FieldLocation;

@RunWith(Theories.class)
public class AssumeCopiedIntoUnmodifiableTest {
    private static final String unusedMessage = "";
    
    private static final FieldLocation collectionTypeFieldLocation = fieldLocation("myCollectionField", from(Dotted.dotted("some.Clazz")));
    
    @DataPoints public static final MutableReasonDetail[] reasons = new MutableReasonDetail[] {
        newMutableReasonDetail(unusedMessage, collectionTypeFieldLocation, ABSTRACT_TYPE_TO_FIELD),
        newMutableReasonDetail(unusedMessage, collectionTypeFieldLocation, ABSTRACT_COLLECTION_TYPE_TO_FIELD),
        newMutableReasonDetail(unusedMessage, collectionTypeFieldLocation, COLLECTION_FIELD_WITH_MUTABLE_ELEMENT_TYPE),
    };
    
    @Theory
    public void matchesReasonsOnGivenField(@DataPoint MutableReasonDetail reasonDetail) throws Exception {
        assertThat(reasonDetail, AssumeCopiedIntoUnmodifiable.assuming("myCollectionField").isSafelyCopiedUnmodifiableCollectionWithImmutableTypes());
    }
    
    @Test
    public void doesntMatchWhenFieldNameIsIncorrect() throws Exception {
        assertThat(reasons[0], not(assuming("myCollectionFieldTHAT_DOESNT_EXIST").isSafelyCopiedUnmodifiableCollectionWithImmutableTypes()));
    }
}
