package org.mutabilitydetector.unittesting.matchers.reasons;

import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Iterables.concat;
import static java.util.Arrays.asList;
import static org.mutabilitydetector.MutabilityReason.ABSTRACT_COLLECTION_TYPE_TO_FIELD;
import static org.mutabilitydetector.MutabilityReason.ABSTRACT_TYPE_TO_FIELD;
import static org.mutabilitydetector.MutabilityReason.ARRAY_TYPE_INHERENTLY_MUTABLE;
import static org.mutabilitydetector.MutabilityReason.COLLECTION_FIELD_WITH_MUTABLE_ELEMENT_TYPE;
import static org.mutabilitydetector.MutabilityReason.FIELD_CAN_BE_REASSIGNED;
import static org.mutabilitydetector.MutabilityReason.MUTABLE_TYPE_TO_FIELD;
import static org.mutabilitydetector.MutabilityReason.NON_FINAL_FIELD;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.locations.FieldLocation;
import org.mutabilitydetector.unittesting.AllowedReason;

/**
 * Allowed reasons for mutability warnings related to fields.
 * <p>
 * It is expected that this class will not be used directly. Instead, use the
 * factory methods provided by {@link AllowedReason} for more fluent unit tests.
 * <p>
 * 
 * @see AllowedReason#assumingFields(Iterable)
 * @see AllowedReason#assumingFields(String, String...)
 */
public final class FieldAssumptions {

    private final Set<String> fieldNames;

    private FieldAssumptions(Set<String> fieldNames) {
        this.fieldNames = Collections.unmodifiableSet(new HashSet<String>(fieldNames));
    }

    /**
     * Advice: use the factory method
     * {@link AllowedReason#assumingFields(String, String...)} for greater
     * readability.
     */
    public static FieldAssumptions named(String firstFieldName, String... otherFieldNames) {
        return named(concat(asList(firstFieldName), asList(otherFieldNames)));
    }

    /**
     * Advice: use the factory method
     * {@link AllowedReason#assumingFields(String, String...)} for greater
     * readability.
     */
    public static FieldAssumptions named(Iterable<String> fieldNames) {
        return new FieldAssumptions(copyOf(fieldNames));
    }

    /**
     * Insists fields of collection types are copied and wrapped safely.
     * <p>
     * One way to use mutable collection types in an immutable class is to copy
     * the contents and wrap in an unmodifiable collection. Mutability Detector
     * has limited support for recognising this pattern, e.g.:
     * <code>Collections.unmodifiableList(new ArrayList(original));</code>.
     * However, the methods used for copying and wrapping must be those
     * available in the JDK. If you are using your own, or third-party
     * collection types, Mutability Detector will raise a warning. This allowed
     * reason will permit those warnings.
     * <p>
     * Example usage:
     * 
     * <pre>
     * <code>
     * import com.google.common.collect.Lists;
     * 
     * &#064;Immutable
     * public final class SafelyCopiesAndWraps {
     *   private final List<String> unmodifiableCopy;
     *   
     *   public SafelyCopiesAndWraps(List<String> original) {
     *     // use Guava method to copy 
     *     this.unmodifiableCopy = Collections.unmodifiableList(Lists.newArrayList(original));
     *   }
     *   
     *   // ... other methods
     * }
     * 
     *  // a warning will be raised because copy method, Guava's Lists.newArrayList(),  is unrecognised
     *  assertImmutable(SafelyCopiesAndWraps.class, areImmutable());
     *  
     *  // use FieldAssumptions to insist the usage is safe
     *  assertInstancesOf(SafelyCopiesAndWraps.class, 
     *                    areImmutable(),
     *                    assumingFields("unmodifiableCopy").areSafelyCopiedUnmodifiableCollectionsWithImmutableElements());
     * </code>
     * </pre>
     * <p>
     * This case will also work when the collection is declared (with generics)
     * to contain a mutable type.
     * 
     * @see MutabilityReason#ABSTRACT_COLLECTION_TYPE_TO_FIELD
     * @see MutabilityReason#ABSTRACT_TYPE_TO_FIELD
     * @see MutabilityReason#COLLECTION_FIELD_WITH_MUTABLE_ELEMENT_TYPE
     */
    public Matcher<MutableReasonDetail> areSafelyCopiedUnmodifiableCollectionsWithImmutableElements() {
        return new AssumeCopiedIntoUnmodifiable();
    }

    private final class AssumeCopiedIntoUnmodifiable extends BaseMutableReasonDetailMatcher {
        @Override
        protected boolean matchesSafely(MutableReasonDetail reasonDetail) {
            return new FieldLocationWithNameMatcher().matches(reasonDetail.codeLocation())
                    && reasonDetail.reason().isOneOf(ABSTRACT_COLLECTION_TYPE_TO_FIELD,
                                                     ABSTRACT_TYPE_TO_FIELD,
                                                     COLLECTION_FIELD_WITH_MUTABLE_ELEMENT_TYPE);
        }
    }

    public Matcher<MutableReasonDetail> areNotModifiedAndDoNotEscape() {
        return new MutableFieldNotModifiedAndDoesntEscapeMatcher();
    }

    private final class MutableFieldNotModifiedAndDoesntEscapeMatcher extends BaseMutableReasonDetailMatcher {
        @Override
        protected boolean matchesSafely(MutableReasonDetail reasonDetail) {
    
            return new FieldLocationWithNameMatcher().matches(reasonDetail.codeLocation())
                    && reasonDetail.reason().isOneOf(MUTABLE_TYPE_TO_FIELD,
                                                     COLLECTION_FIELD_WITH_MUTABLE_ELEMENT_TYPE,
                                                     ARRAY_TYPE_INHERENTLY_MUTABLE);
        }
    }

    public Matcher<MutableReasonDetail> areModifiedAsPartOfAnUnobservableCachingStrategy() {
        return new FieldModifiedAsPartOfAnUnobservableCachingStrategy();
    }

    private final class FieldModifiedAsPartOfAnUnobservableCachingStrategy extends BaseMutableReasonDetailMatcher {
        @Override
        protected boolean matchesSafely(MutableReasonDetail reasonDetail) {

            return new FieldLocationWithNameMatcher().matches(reasonDetail.codeLocation())
                    && reasonDetail.reason().isOneOf(MUTABLE_TYPE_TO_FIELD,
                                                     COLLECTION_FIELD_WITH_MUTABLE_ELEMENT_TYPE,
                                                     ARRAY_TYPE_INHERENTLY_MUTABLE,
                                                     FIELD_CAN_BE_REASSIGNED,
                                                     NON_FINAL_FIELD);
        }
    }

    private class FieldLocationWithNameMatcher extends TypeSafeMatcher<FieldLocation> {
        @Override
        public void describeTo(Description description) {
        }
    
        @Override
        protected boolean matchesSafely(FieldLocation locationOfMutability) {
            return fieldNames.contains(locationOfMutability.fieldName());
        }
    
    }
}