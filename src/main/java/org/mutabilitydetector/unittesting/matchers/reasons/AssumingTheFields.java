package org.mutabilitydetector.unittesting.matchers.reasons;

import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Iterables.concat;
import static java.util.Arrays.asList;
import static org.mutabilitydetector.MutabilityReason.COLLECTION_FIELD_WITH_MUTABLE_ELEMENT_TYPE;
import static org.mutabilitydetector.MutabilityReason.MUTABLE_TYPE_TO_FIELD;

import java.util.Set;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.locations.CodeLocation;
import org.mutabilitydetector.locations.FieldLocation;

public final class AssumingTheFields  {
    
    private final Set<String> fieldNames;

    private AssumingTheFields(Set<String> fieldNames) {
        this.fieldNames = fieldNames;
    }
    
    public static AssumingTheFields named(String first, String... rest) {
        return new AssumingTheFields(copyOf(concat(asList(first), asList(rest))));
    }

    public static AssumingTheFields assumingFieldsNamed(String first, String... rest) {
        return new AssumingTheFields(copyOf(concat(asList(first), asList(rest))));
    }
    
    public Matcher<MutableReasonDetail> areNotModified() {
        return isMutableFieldWithName();
    }
    
    public Matcher<MutableReasonDetail> areModifiedAsPartAsAnUnobservableCachingStrategy() {
        return isMutableFieldWithName();
    }

    private TypeSafeDiagnosingMatcher<MutableReasonDetail> isMutableFieldWithName() {
        return new TypeSafeDiagnosingMatcher<MutableReasonDetail>() {
            @Override public void describeTo(Description description) { }

            @Override
            protected boolean matchesSafely(MutableReasonDetail item, Description mismatchDescription) {
                CodeLocation<?> locationOfMutability = item.codeLocation();
                if (locationOfMutability instanceof FieldLocation) {
                    return item.reason().isOneOf(MUTABLE_TYPE_TO_FIELD, COLLECTION_FIELD_WITH_MUTABLE_ELEMENT_TYPE)
                            && fieldNames.contains(((FieldLocation)locationOfMutability).fieldName());
                } else {
                    return false;
                }
            }

        };
    }
}