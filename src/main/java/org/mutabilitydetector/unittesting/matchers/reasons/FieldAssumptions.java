package org.mutabilitydetector.unittesting.matchers.reasons;

import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Iterables.concat;
import static java.util.Arrays.asList;
import static org.mutabilitydetector.MutabilityReason.ABSTRACT_COLLECTION_TYPE_TO_FIELD;
import static org.mutabilitydetector.MutabilityReason.ABSTRACT_TYPE_TO_FIELD;
import static org.mutabilitydetector.MutabilityReason.ARRAY_TYPE_INHERENTLY_MUTABLE;
import static org.mutabilitydetector.MutabilityReason.COLLECTION_FIELD_WITH_MUTABLE_ELEMENT_TYPE;
import static org.mutabilitydetector.MutabilityReason.MUTABLE_TYPE_TO_FIELD;

import java.util.Set;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.locations.FieldLocation;

public final class FieldAssumptions  {
    
    private final Set<String> fieldNames;

    private FieldAssumptions(Set<String> fieldNames) {
        this.fieldNames = fieldNames;
    }
    
    public static FieldAssumptions named(String firstFieldName, String... otherFieldNames) {
        return named(concat(asList(firstFieldName), asList(otherFieldNames)));
    }

    public static FieldAssumptions named(Iterable<String> fieldNames) {
        return new FieldAssumptions(copyOf(fieldNames));
    }

    public Matcher<MutableReasonDetail> areSafelyCopiedUnmodifiableCollectionWithImmutableTypes() {
        return new AssumeCopiedIntoUnmodifiable();
    }

    public Matcher<MutableReasonDetail> areNotModifiedAndDoNotEscape() {
        return new MutableFieldMatcher();
    }
    
    public Matcher<MutableReasonDetail> areModifiedAsPartAsAnUnobservableCachingStrategy() {
        return new MutableFieldMatcher();
    }
    
    private class FieldLocationWithNameMatcher extends TypeSafeMatcher<FieldLocation> {
        @Override public void describeTo(Description description) { }

        @Override
        protected boolean matchesSafely(FieldLocation locationOfMutability) {
            return fieldNames.contains(locationOfMutability.fieldName());
        }
        
    }
    
    private class MutableFieldMatcher extends BaseMutableReasonDetailMatcher {
        @Override protected boolean matchesSafely(MutableReasonDetail reasonDetail) {
            
            return new FieldLocationWithNameMatcher().matches(reasonDetail.codeLocation())
                    && reasonDetail.reason().isOneOf(MUTABLE_TYPE_TO_FIELD, 
                                                     COLLECTION_FIELD_WITH_MUTABLE_ELEMENT_TYPE,
                                                     ARRAY_TYPE_INHERENTLY_MUTABLE);
        }
    }
    
    private class AssumeCopiedIntoUnmodifiable extends BaseMutableReasonDetailMatcher {
        @Override protected boolean matchesSafely(MutableReasonDetail reasonDetail) {
            return new FieldLocationWithNameMatcher().matches(reasonDetail.codeLocation())
                    && reasonDetail.reason().isOneOf(ABSTRACT_COLLECTION_TYPE_TO_FIELD, 
                                                     ABSTRACT_TYPE_TO_FIELD, 
                                                     COLLECTION_FIELD_WITH_MUTABLE_ELEMENT_TYPE);
        }
    }
}