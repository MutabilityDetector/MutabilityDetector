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



import static org.mutabilitydetector.MutabilityReason.ABSTRACT_COLLECTION_TYPE_TO_FIELD;
import static org.mutabilitydetector.MutabilityReason.ABSTRACT_TYPE_TO_FIELD;
import static org.mutabilitydetector.MutabilityReason.COLLECTION_FIELD_WITH_MUTABLE_ELEMENT_TYPE;

import org.hamcrest.Matcher;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.locations.CodeLocation.FieldLocation;

public class AssumeCopiedIntoUnmodifiable {

    public static AssumeCopiedIntoUnmodifiable assuming(String fieldName) {
        return new AssumeCopiedIntoUnmodifiable(fieldName);
    }

    private final String fieldName;
    
    public AssumeCopiedIntoUnmodifiable(String fieldName) {
        this.fieldName = fieldName;
    }
    
    public Matcher<MutableReasonDetail> isSafelyCopiedUnmodifiableCollectionWithImmutableTypes() {
        return new BaseMutableReasonDetailMatcher() {
            
            @Override
            protected boolean matchesSafely(MutableReasonDetail reasonDetail) {
                if (reasonDetail.reason().isOneOf(ABSTRACT_COLLECTION_TYPE_TO_FIELD, 
                                                  ABSTRACT_TYPE_TO_FIELD, 
                                                  COLLECTION_FIELD_WITH_MUTABLE_ELEMENT_TYPE)) {
                    String potentiallyAbstractField = ((FieldLocation) reasonDetail.codeLocation()).fieldName();
                    if (potentiallyAbstractField.equals(fieldName)) {
                        return true;
                    }
                }
                
                return false;
            }
        };
    }
}
