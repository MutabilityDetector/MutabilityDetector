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

import static org.mutabilitydetector.MutabilityReason.ABSTRACT_TYPE_INHERENTLY_MUTABLE;
import static org.mutabilitydetector.MutabilityReason.CAN_BE_SUBCLASSED;
import static org.mutabilitydetector.MutabilityReason.NOT_DECLARED_FINAL;

import org.hamcrest.Description;
import org.mutabilitydetector.MutableReasonDetail;

public class AllowingForSubclassing extends BaseMutableReasonDetailMatcher {

    @Override
    public void describeTo(Description description) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @SuppressWarnings("deprecation")
    @Override
    protected boolean matchesSafely(MutableReasonDetail checkerReasonDetail, Description mismatchDescription) {
        return checkerReasonDetail.reason().isOneOf(CAN_BE_SUBCLASSED, 
                                                    NOT_DECLARED_FINAL, 
                                                    ABSTRACT_TYPE_INHERENTLY_MUTABLE);
    }

}

