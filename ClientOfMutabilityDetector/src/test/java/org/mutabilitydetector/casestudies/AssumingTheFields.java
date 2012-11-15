package org.mutabilitydetector.casestudies;

import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Iterables.concat;
import static java.util.Arrays.asList;
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
    
    Matcher<MutableReasonDetail> areNotModifiedByCallers() {
        return new TypeSafeDiagnosingMatcher<MutableReasonDetail>() {
            @Override public void describeTo(Description description) { }

            @Override
            protected boolean matchesSafely(MutableReasonDetail item, Description mismatchDescription) {
                return isMutableFieldWithName(item);
            }
        };
    }
    
    Matcher<MutableReasonDetail> areModifiedAsPartAsAnUnobservableCachingStrategy() {
        return new TypeSafeDiagnosingMatcher<MutableReasonDetail>() {
            @Override public void describeTo(Description description) { }

            @Override
            protected boolean matchesSafely(MutableReasonDetail item, Description mismatchDescription) {
                return isMutableFieldWithName(item);
            }

        };
    }

    private boolean isMutableFieldWithName(MutableReasonDetail reasonDetail) {
        CodeLocation<?> locationOfMutability = reasonDetail.codeLocation();
        if (locationOfMutability instanceof FieldLocation) {
            return reasonDetail.reason().isOneOf(MUTABLE_TYPE_TO_FIELD)
                    && fieldNames.contains(((FieldLocation)locationOfMutability).fieldName());
        } else {
            return false;
        }
    }
}