package org.mutabilitydetector.unittesting.matchers.reasons;

/*
 * #%L
 * MutabilityDetector
 * %%
 * Copyright (C) 2008 - 2014 Graham Allan
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */



import static com.google.common.collect.Iterables.transform;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.mutabilitydetector.MutabilityReason.ABSTRACT_TYPE_TO_FIELD;
import static org.mutabilitydetector.MutabilityReason.COLLECTION_FIELD_WITH_MUTABLE_ELEMENT_TYPE;
import static org.mutabilitydetector.MutabilityReason.MUTABLE_TYPE_TO_FIELD;

import org.hamcrest.Matcher;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.checkers.MutableTypeToFieldChecker;
import org.mutabilitydetector.locations.Dotted;

import com.google.common.collect.Iterables;

public final class ProvidedOtherClass {

    private final Iterable<Dotted> dottedClassNames;

    private ProvidedOtherClass(Iterable<Dotted> dottedClassName) {
        this.dottedClassNames = dottedClassName;
    }

    public static ProvidedOtherClass provided(Dotted className) {
        return provided(singleton(className));
    }
    
    public static ProvidedOtherClass provided(Dotted... classNames) {
        return provided(asList(classNames));
    }

    public static ProvidedOtherClass provided(Iterable<Dotted> classNames) {
        return new ProvidedOtherClass(classNames);
    }

    /**
     * Assumes that the selected type is immutable, preventing warnings related
     * to transitive mutability.
     * <p>
     * One common way for classes to be rendered mutable is to contain a mutable
     * field. Another way is that the class is extensible (non-final). The
     * interaction of these rules can occasionally conflict with the notion of
     * abstraction. For example, consider the following classes:
     * 
     * <pre>
     * </code>
     *  // implementations MUST be immutable
     *  public interface Named {
     *      String getName();
     *  }
     *  
     *  public final class HasSomethingNamed {
     *      private final Named named;
     *      public HasSomethingNamed(Named named) {
     *          this.named = named;
     *      }
     *  
     *      public String getNameOfYourThing() {
     *          return this.named.getName();
     *      }
     *  }
     * </code>
     * </pre>
     * 
     * In this contrived example, the interface Named is abstracting something.
     * It would be preferable to be able to depend on that abstraction, rather
     * than a concrete implementation. Unfortunately, any implementation of
     * Named <strong>could</strong> violate the condition that it must be
     * immutable. If the Named implementation given to the constructor of
     * HasSomethingNamed is actually mutable, it causes HasSomething named to be
     * mutable as well. Consider this code:
     * 
     * <pre>
     * </code>
     * SneakyMutableNamed n = new SneakyMutableNamed("Jimmy");
     * HasSomethingNamed h = new HasSomethingNamed(n);
     * 
     * String nameOnFirstCall = h.getNameOfYourThing();
     * n.myReassignableName = "Bobby";
     * String nameOnSecondCall = h.getNameOfYourThing();
     * </code>
     * </pre>
     * 
     * Here, because a sneaky subclass of Named is mutated, the instance of
     * HasSomethingNamed has been observed to change (getNameOfYourThing() first
     * returns "Jimmy" then "Bobby").
     * <p>
     * Despite this limitation, it can still be preferable that the abstract
     * class is given as a parameter. Perhaps you are able to trust that all
     * implementations <strong>are</strong> immutable. In that case, Mutability
     * Detector raising a warning on HasSomethingNamed would be considered a
     * false positive. This reason allows the test to pass.
     * <p>
     * Example usage:
     * 
     * <pre><code>
     * assertInstancesOf(HasSomethingNamed.class,
     *                   areImmutable(),
     *                   AllowedReason.provided(Named.class).isAlsoImmutable());
     * </pre></code>
     * 
     * Not that this also allows a field which is a collection type, with Named as a generic element type.
     */
    public Matcher<MutableReasonDetail> isAlsoImmutable() {
        final Matcher<MutableReasonDetail> allowGenericTypes = new AllowedIfOtherClassIsGenericTypeOfCollectionField(dottedClassNames);

        return anyOf(allowGenericTypes, anyOf(transform(dottedClassNames, AllowedIfOtherClassIsImmutable::new)));
    }

    /**
     * Assumes that the selected type is immutable, preventing warnings related
     * to transitive mutability.
     * <p>
     * One common way for classes to be rendered mutable is to contain a mutable
     * field. Another way is that the class is extensible (non-final). The
     * interaction of these rules can occasionally conflict with the notion of
     * abstraction. For example, consider the following classes:
     * 
     * <pre>
     * </code>
     *  // implementations MUST be immutable
     *  public interface Named {
     *      String getName();
     *  }
     *  
     *  public final class HasSomethingNamed {
     *      private final Named named;
     *      public HasSomethingNamed(Named named) {
     *          this.named = named;
     *      }
     *  
     *      public String getNameOfYourThing() {
     *          return this.named.getName();
     *      }
     *  }
     * </code>
     * </pre>
     * 
     * In this contrived example, the interface Named is abstracting something.
     * It would be preferable to be able to depend on that abstraction, rather
     * than a concrete implementation. Unfortunately, any implementation of
     * Named <strong>could</strong> violate the condition that it must be
     * immutable. If the Named implementation given to the constructor of
     * HasSomethingNamed is actually mutable, it causes HasSomething named to be
     * mutable as well. Consider this code:
     * 
     * <pre>
     * </code>
     * SneakyMutableNamed n = new SneakyMutableNamed("Jimmy");
     * HasSomethingNamed h = new HasSomethingNamed(n);
     * 
     * String nameOnFirstCall = h.getNameOfYourThing();
     * n.myReassignableName = "Bobby";
     * String nameOnSecondCall = h.getNameOfYourThing();
     * </code>
     * </pre>
     * 
     * Here, because a sneaky subclass of Named is mutated, the instance of
     * HasSomethingNamed has been observed to change (getNameOfYourThing() first
     * returns "Jimmy" then "Bobby)".
     * <p>
     * Despite this limitation, it can still be preferable that the abstract
     * class is given as a parameter. Perhaps you are able to trust that all
     * implementations <strong>are</strong> immutable. In that case, Mutability
     * Detector raising a warning on HasSomethingNamed would be considered a
     * false positive. This reason allows the test to pass.
     * <p>
     * Example usage:
     * 
     * <pre><code>
     * assertInstancesOf(HasSomethingNamed.class,
     *                   areImmutable(),
     *                   AllowedReason.provided(Named.class).areAlsoImmutable());
     * </pre></code>
     * 
     * Not that this also allows a field which is a collection type, with Named as a generic element type.
     */
    public Matcher<MutableReasonDetail> areAlsoImmutable() {
        return isAlsoImmutable();
    }

    private static final class AllowedIfOtherClassIsImmutable extends BaseMutableReasonDetailMatcher {

        private final Dotted className;

        public AllowedIfOtherClassIsImmutable(Dotted dottedClassName) {
            this.className = dottedClassName;
        }

        @Override
        protected boolean matchesSafely(MutableReasonDetail reasonDetail) {
            return isAssignedField(reasonDetail);
        }

        private boolean isAssignedField(MutableReasonDetail reasonDetail) {
            return reasonDetail.reason().isOneOf(ABSTRACT_TYPE_TO_FIELD, MUTABLE_TYPE_TO_FIELD)
                    && reasonDetail.message().contains(classNameAsItAppearsInDescription());
        }

        /**
         * This matcher has to check against string created by the checker, which may change.
         * @see MutableTypeToFieldChecker
         */
        private String classNameAsItAppearsInDescription() {
            return "(" + className.asString() + ")";
        }

    }

    private static final class AllowedIfOtherClassIsGenericTypeOfCollectionField extends BaseMutableReasonDetailMatcher {
        
        private final Iterable<Dotted> classNames;
        
        public AllowedIfOtherClassIsGenericTypeOfCollectionField(Iterable<Dotted> classNames) {
            this.classNames = classNames;
        }
        
        @Override
        protected boolean matchesSafely(MutableReasonDetail reasonDetail) {
            return allowedIfCollectionTypeWhereAllGenericElementsAreConsideredImmutable(reasonDetail);
        }
        
        private boolean allowedIfCollectionTypeWhereAllGenericElementsAreConsideredImmutable(MutableReasonDetail reasonDetail) {
            return reasonDetail.reason().isOneOf(COLLECTION_FIELD_WITH_MUTABLE_ELEMENT_TYPE)
                    && allElementTypesAreConsideredImmutable(reasonDetail.message());
        }

        /**
         * This matcher has to check against string created by the checker, which may change.
         * @see MutableTypeToFieldChecker
         */
        private boolean allElementTypesAreConsideredImmutable(String message) {
            String fieldTypeDescription = message.substring(message.indexOf("("), message.indexOf(")") + 1);
            String generics = fieldTypeDescription.substring(fieldTypeDescription.indexOf("<") + 1, fieldTypeDescription.lastIndexOf(">"));
            
            String[] genericsTypesDescription = generics.contains(", ") 
                    ? generics.split(", ")
                    : new String[] { generics };
            
            for (String genericType : genericsTypesDescription) {
                if (!Iterables.contains(classNames, Dotted.dotted(genericType))) {
                    return false;
                }
            }
            return true;
        }
    }
}
