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
package org.mutabilitydetector;

import static java.lang.String.format;
import static org.mutabilitydetector.TestUtil.formatReasons;

import java.util.Arrays;
import java.util.Collection;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.Ignore;
import org.mutabilitydetector.checkers.AsmMutabilityChecker;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 *         license/LICENSE.txt
 */

@Ignore
public class TestMatchers {

    public static Matcher<? super AsmMutabilityChecker> hasReasons() {
        return new TypeSafeDiagnosingMatcher<AsmMutabilityChecker>() {

            @Override
            protected boolean matchesSafely(AsmMutabilityChecker item, Description mismatchDescription) {
                mismatchDescription.appendText(" got a checker (" + item.toString() + ") containing zero reasons ");
                return !(item.reasons().isEmpty());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(" a checker reporting at least one reason ");
            }

        };
    }
    
    private static Function<MutableReasonDetail, Reason> TO_REASON = new Function<MutableReasonDetail, Reason>() {
        @Override public Reason apply(MutableReasonDetail input) {
            return input.reason();
        }
    };

    public static Matcher<? super AsmMutabilityChecker> hasReasons(final Reason... reasons) {
        return new TypeSafeDiagnosingMatcher<AsmMutabilityChecker>() {
            
            @Override
            protected boolean matchesSafely(AsmMutabilityChecker item, Description mismatchDescription) {
                
                Collection<Reason> actualReasons = Collections2.transform(item.checkerResult().reasons, TO_REASON);
                
                if (!actualReasons.containsAll(Arrays.asList(reasons))) {
                    mismatchDescription.appendText(" got a checker containing reasons: ")
                                       .appendValueList("[", " ", "]", actualReasons);
                    return false;
                }
                return true;
            }
            
            @Override
            public void describeTo(Description description) {
                description.appendText(" a checker reporting reasons including " + Arrays.toString(reasons) + " ");
            }
            
        };
    }
    

    public static Matcher<? super AsmMutabilityChecker> hasNoReasons() {
        return new TypeSafeDiagnosingMatcher<AsmMutabilityChecker>() {

            @Override
            protected boolean matchesSafely(AsmMutabilityChecker checker, Description mismatchDescription) {
                String mismatch = format(" got a checker containing %d reasons, %n%s",
                        checker.reasons().size(),
                        formatReasons(checker.reasons()));
                mismatchDescription.appendText(mismatch);
                return (checker.reasons().isEmpty());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(" a checker reporting zero reasons ");
            }

        };
    }

}
