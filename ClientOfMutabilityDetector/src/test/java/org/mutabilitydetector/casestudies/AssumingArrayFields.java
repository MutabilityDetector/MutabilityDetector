package org.mutabilitydetector.casestudies;

import static java.util.Arrays.asList;
import static org.mutabilitydetector.MutabilityReason.ARRAY_TYPE_INHERENTLY_MUTABLE;
import static org.mutabilitydetector.MutabilityReason.MUTABLE_TYPE_TO_FIELD;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.locations.FieldLocation;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

public class AssumingArrayFields {
    private ImmutableSet<String> fieldNames;

    public AssumingArrayFields(ImmutableSet<String> fieldNames) {
        this.fieldNames = fieldNames;
    }

    public static AssumingArrayFields named(String first, String... rest) {
        return new AssumingArrayFields(ImmutableSet.copyOf(Iterables.concat(asList(first), asList(rest))));
    }
    
    public Matcher<MutableReasonDetail> areNotModifiedAndDoNotEscape() {
        return new TypeSafeDiagnosingMatcher<MutableReasonDetail>() {

            @Override public void describeTo(Description description) { }

            @Override
            protected boolean matchesSafely(MutableReasonDetail reasonDetail, Description mismatchDescription) {
                if (reasonDetail.codeLocation() instanceof FieldLocation) {
                    return reasonDetail.reason().isOneOf(MUTABLE_TYPE_TO_FIELD, ARRAY_TYPE_INHERENTLY_MUTABLE)
                            && fieldNames.contains(((FieldLocation)reasonDetail.codeLocation()).fieldName());
                } else {
                    return false;
                }
            }
            
        };
    }
}