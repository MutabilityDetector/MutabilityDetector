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

import static org.mutabilitydetector.MutabilityReason.ABSTRACT_TYPE_TO_FIELD;
import static org.mutabilitydetector.MutabilityReason.MUTABLE_TYPE_TO_FIELD;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.checkers.AbstractTypeToFieldChecker;
import org.mutabilitydetector.locations.Dotted;

public class ProvidedOtherClass {

    private final Dotted dottedClassName;

    private ProvidedOtherClass(Dotted dottedClassName) {
        this.dottedClassName = dottedClassName;
    }

    public static ProvidedOtherClass provided(Dotted className) {
        return new ProvidedOtherClass(className);
    }

    public Matcher<MutableReasonDetail> isAlsoImmutable() {
        return new AllowedIfOtherClassIsImmutable(dottedClassName);
    }

    private static class AllowedIfOtherClassIsImmutable extends TypeSafeDiagnosingMatcher<MutableReasonDetail> {

        private final Dotted className;

        public AllowedIfOtherClassIsImmutable(Dotted dottedClassName) {
            this.className = dottedClassName;
        }

        @Override
        public void describeTo(Description description) {
            throw new UnsupportedOperationException("not implemented yet");
        }

        @Override
        protected boolean matchesSafely(MutableReasonDetail reasonDetail, Description mismatchDescription) {
            return reasonDetail.reason().isOneOf(ABSTRACT_TYPE_TO_FIELD, MUTABLE_TYPE_TO_FIELD)
                    && reasonDetail.message().contains(classNameAsItAppearsInDescription());
        }
        
        /**
         * This matcher has to check against string created by the checker, which may change.
         * @see AbstractTypeToFieldChecker
         */
        private String classNameAsItAppearsInDescription() {
            return "(" + className.asString() + ")";
        }

    }
}
