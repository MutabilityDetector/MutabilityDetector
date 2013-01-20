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
     *   private final List&lt;String&gt; unmodifiableCopy;
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
     *  assertInstancesOf(SafelyCopiesAndWraps.class, areImmutable());
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

    /**
     * Insists that a mutable field is used safely.
     * <p>
     * A requirement for immutability is
     * "If the instance fields include references to mutable objects, don't allow those objects to be changed"
     * [0]. This necessitates that any mutable fields are not modified (e.g. by
     * calling a method which mutates it) and their reference is not published
     * (where client code could invoke a mutating method). While greater care is
     * needed, it is possible to create immutable objects composed of mutable
     * fields.
     * <p>
     * Example usage:
     * 
     * <pre>
     * <code>
     * import java.util.Date;
     * 
     * &#064;Immutable
     * public final class UsesMutableField {
     *   private final Date myDate;
     *   
     *   public UsesMutableField(Date original) {
     *     this.myDate = new Date(original.getTime());
     *   }
     *   
     *   public Date getDate() {
     *     // if we used 'return myDate;' we would be publishing reference
     *     return new Date(myDate.getTime());
     *   }
     *   
     *   // ... other methods, which never call myDate.setTime()
     *   // if we called, e.g. setTime() we would be mutating the field
     * }
     * 
     *  // a warning will be raised because myDate is of a mutable type, java.util.Date
     *  assertInstancesOf(UsesMutableField.class, areImmutable());
     *  
     *  // use FieldAssumptions to insist the usage is safe
     *  assertInstancesOf(UsesMutableField.class, 
     *                    areImmutable(),
     *                    assumingFields("myDate").areNotModifiedAndDoNotEscape());
     * </code>
     * </pre>
     * <p>
     * Note: this allowed reason also assumes the defensive copy of
     * <code>original</code> into <code>myDate</code>, although there is
     * currently no support for automatically detecting this.
     * 
     * <p>
     * [0] <a href="http://docs.oracle.com/javase/tutorial/essential/concurrency/imstrat">
     *       A Strategy for Defining Immutable Objects</a>
     * 
     * @see MutabilityReason#MUTABLE_TYPE_TO_FIELD
     * @see MutabilityReason#COLLECTION_FIELD_WITH_MUTABLE_ELEMENT_TYPE
     * @see MutabilityReason#ARRAY_TYPE_INHERENTLY_MUTABLE
     */
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

    /**
     * Insists that while a field may have been mutated, changes will not be
     * observable.
     * <p>
     * As described in the documentation for Java Concurrency In Practice's
     * &#064;Immutable annotation, objects may maintain mutable state, as long
     * as the mutation is internal, and cannot be observed by callers. This can
     * be useful for providing caching within the object instance. A classic
     * example of this is the Open JDK's implementation of
     * {@link java.lang.String}. Each instance uses the <code>hash</code> field
     * to cache the result of {@link #hashCode()}. The hash field is computed
     * lazily, and the field is reassigned (a mutation), however clients of
     * {@link String} can not observe the mutation as it is internal.
     * <p>
     * While this technique is tricky, it can be very useful for performance
     * reasons. Unfortunately, Mutability Detector cannot tell the difference
     * between: lazily storing the result of a computation for future lookup;
     * and a setter method. 
     * <p>
     * This allowed reason will permit mutable fields, and also reassigning field references.
     * <p>
     * Example usage:
     * <pre>
     * <code>
     * import java.util.Date;
     * 
     * &#064;Immutable
     * public static final class ReassignsHashCode {
     *   private final String name;
     *   private final Integer age;
     *   private int hash;
     *     
     *   public ReassignsHashCode(String name, Integer age) {
     *     this.name = name;
     *     this.age = age;
     *   }
     *     
     *   &#064;Override
     *   public int hashCode() {
     *     if (hash == 0) {
     *         hash = name.hashCode() + age.hashCode();
     *     }
     *     return hash;
     *   }
     * }
     * 
     *  // a warning will be raised because the hash field is reassigned, as with a setter method
     *  assertInstancesOf(ReassignsHashCode.class, areImmutable());
     *  
     *  // use FieldAssumptions to insist the usage is safe
     *  assertInstancesOf(ReassignsHashCode.class, 
     *                    areImmutable(),
     *                    assumingFields("hash").areModifiedAsPartOfAnUnobservableCachingStrategy());
     * </code>
     * </pre>
     * 
     * @see MutabilityReason#MUTABLE_TYPE_TO_FIELD
     * @see MutabilityReason#COLLECTION_FIELD_WITH_MUTABLE_ELEMENT_TYPE
     * @see MutabilityReason#ARRAY_TYPE_INHERENTLY_MUTABLE
     * @see MutabilityReason#FIELD_CAN_BE_REASSIGNED
     * @see MutabilityReason#NON_FINAL_FIELD
     */
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