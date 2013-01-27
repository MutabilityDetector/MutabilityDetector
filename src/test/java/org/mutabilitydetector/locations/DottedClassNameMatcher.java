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
package org.mutabilitydetector.locations;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.mutabilitydetector.locations.Dotted;

public final class DottedClassNameMatcher extends TypeSafeDiagnosingMatcher<Dotted> {

    private final String expectedName;

    private DottedClassNameMatcher(String expectedName) {
        this.expectedName = expectedName;
    }
    
    public static DottedClassNameMatcher aDottedClassNameOf(String expected) {
        return new DottedClassNameMatcher(expected);
    }
    
    @Override
    public void describeTo(Description description) {
        description.appendText("a Dotted class name of ").appendValue(expectedName);
    }

    @Override
    protected boolean matchesSafely(Dotted dotted, Description mismatchDescription) {
        if (!dotted.asString().equals(expectedName)) {
            mismatchDescription.appendText("a Dotted class name of ").appendValue(dotted.asString());
            return false;
        } else {
            return true;
        }
    }
    
}