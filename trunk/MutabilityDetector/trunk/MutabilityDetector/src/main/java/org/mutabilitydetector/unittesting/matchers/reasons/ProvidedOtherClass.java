/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector.unittesting.matchers.reasons;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.mutabilitydetector.CheckerReasonDetail;
import org.mutabilitydetector.MutabilityReason;
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

    public Matcher<CheckerReasonDetail> isAlsoImmutable() {
        return new AllowedIfOtherClassIsImmutable(dottedClassName);
    }

    private static class AllowedIfOtherClassIsImmutable extends TypeSafeDiagnosingMatcher<CheckerReasonDetail> {

        private final Dotted className;

        public AllowedIfOtherClassIsImmutable(Dotted dottedClassName) {
            this.className = dottedClassName;
        }

        @Override
        public void describeTo(Description description) {
            throw new UnsupportedOperationException("not implemented yet");
        }

        @Override
        protected boolean matchesSafely(CheckerReasonDetail reasonDetail, Description mismatchDescription) {
            return reasonDetail.reason() == MutabilityReason.ABSTRACT_TYPE_TO_FIELD
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
