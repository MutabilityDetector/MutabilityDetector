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

import static com.google.common.collect.Iterables.transform;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.mutabilitydetector.MutabilityReason.ABSTRACT_TYPE_TO_FIELD;
import static org.mutabilitydetector.MutabilityReason.COLLECTION_FIELD_WITH_MUTABLE_ELEMENT_TYPE;
import static org.mutabilitydetector.MutabilityReason.MUTABLE_TYPE_TO_FIELD;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.checkers.MutableTypeToFieldChecker;
import org.mutabilitydetector.locations.Dotted;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public class ProvidedOtherClass {

    private final Iterable<Dotted> dottedClassNames;

    private ProvidedOtherClass(Iterable<Dotted> dottedClassName) {
        this.dottedClassNames = dottedClassName;
    }

    public static ProvidedOtherClass provided(Dotted className) {
        return provided(singleton(className));
    }
    
    public static ProvidedOtherClass provided(Dotted... className) {
        return provided(asList(className));
    }

    public static ProvidedOtherClass provided(Iterable<Dotted> classNames) {
        return new ProvidedOtherClass(classNames);
    }

    public Matcher<MutableReasonDetail> isAlsoImmutable() {
        final Matcher<MutableReasonDetail> allowGenericTypes = new AllowedIfOtherClassIsGenericTypeOfCollectionField(dottedClassNames);
        
        return anyOf(allowGenericTypes, anyOf(transform(dottedClassNames, toMatcher())));
    }

    private static final Function<Dotted, Matcher<? super MutableReasonDetail>> toMatcher() {
        return new Function<Dotted, Matcher<? super MutableReasonDetail>>() { 
            @Override public Matcher<MutableReasonDetail> apply(Dotted input) {
                return new AllowedIfOtherClassIsImmutable(input);
            }
        };
    }

    private static final class AllowedIfOtherClassIsImmutable extends TypeSafeDiagnosingMatcher<MutableReasonDetail> {

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
            return isAssignedField(reasonDetail);
        }

        private boolean isAssignedField(MutableReasonDetail reasonDetail) {
            return reasonDetail.reason().isOneOf(ABSTRACT_TYPE_TO_FIELD, MUTABLE_TYPE_TO_FIELD)
                    && reasonDetail.message().contains(classNameAsItAppearsInDescription());
        }

        /**
         * This matcher has to check against string created by the checker, which may change.
         * @see MutableTypeToFieldChecker
         */
        private String classNameAsItAppearsInDescription() {
            return "(" + className.asString() + ")";
        }

    }
    
    private static final class AllowedIfOtherClassIsGenericTypeOfCollectionField extends TypeSafeDiagnosingMatcher<MutableReasonDetail> {
        
        private final Iterable<Dotted> classNames;
        
        public AllowedIfOtherClassIsGenericTypeOfCollectionField(Iterable<Dotted> classNames) {
            this.classNames = classNames;
        }
        
        @Override
        public void describeTo(Description description) {
            throw new UnsupportedOperationException("not implemented yet");
        }
        
        @Override
        protected boolean matchesSafely(MutableReasonDetail reasonDetail, Description mismatchDescription) {
            return allowedIfCollectionTypeWhereAllGenericElementsAreConsideredImmutable(reasonDetail);
        }
        
        private boolean allowedIfCollectionTypeWhereAllGenericElementsAreConsideredImmutable(MutableReasonDetail reasonDetail) {
            return reasonDetail.reason().isOneOf(COLLECTION_FIELD_WITH_MUTABLE_ELEMENT_TYPE)
                    && allElementTypesAreConsideredImmutable(reasonDetail.message());
        }

        /**
         * This matcher has to check against string created by the checker, which may change.
         * @see MutableTypeToFieldChecker
         */
        private boolean allElementTypesAreConsideredImmutable(String message) {
            String fieldTypeDescription = message.substring(message.indexOf("("), message.indexOf(")") + 1);
            String generics = fieldTypeDescription.substring(fieldTypeDescription.indexOf("<") + 1, fieldTypeDescription.lastIndexOf(">"));
            
            String[] genericsTypesDescription = generics.contains(", ") 
                    ? generics.split(", ")
                    : new String[] { generics };        
            
            for (String genericType : genericsTypesDescription) {
                if (!Iterables.contains(classNames, Dotted.dotted(genericType))) {
                    return false;
                }
            }
            return true;
        }
    }
}
