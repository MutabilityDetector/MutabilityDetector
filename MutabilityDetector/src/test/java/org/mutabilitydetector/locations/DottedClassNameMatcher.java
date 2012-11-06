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