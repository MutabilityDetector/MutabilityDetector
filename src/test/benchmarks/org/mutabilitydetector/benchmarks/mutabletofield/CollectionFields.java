package org.mutabilitydetector.benchmarks.mutabletofield;

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

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

import org.mutabilitydetector.benchmarks.ImmutableExample;

@SuppressWarnings("unused")
public class CollectionFields {

    public static final class CopyListIntoNewArrayListAndUnmodifiableListIdiom {

        private final List<ImmutableExample> unmodifiable;

        public CopyListIntoNewArrayListAndUnmodifiableListIdiom(List<ImmutableExample> potentiallyMutatable) {
            this.unmodifiable = Collections.unmodifiableList(new ArrayList<ImmutableExample>(potentiallyMutatable));
        }

        public List<ImmutableExample> getUnmodifiable() {
            return unmodifiable;
        }
    }

    public final static class StaticMethodDoesTheCopying {
        private final List<ImmutableExample> unmodifiable;

        private StaticMethodDoesTheCopying(List<ImmutableExample> unmodifiable) {
            this.unmodifiable = unmodifiable;
        }

        public static StaticMethodDoesTheCopying create(List<ImmutableExample> potentiallyMutatable) {
            return new StaticMethodDoesTheCopying(Collections.unmodifiableList(new ArrayList<ImmutableExample>(potentiallyMutatable)));
        }

        public List<ImmutableExample> getUnmodifiable() {
            return unmodifiable;
        }

    }

    public final static class StoresCopiedCollectionIntoLocalVariableBeforeWrapping {
        private final SortedSet<ImmutableExample> unmodifiable;

        public StoresCopiedCollectionIntoLocalVariableBeforeWrapping(SortedSet<ImmutableExample> potentiallyMutatable) {
            ConcurrentSkipListSet<ImmutableExample> iCouldEscapeAndBeModified = new ConcurrentSkipListSet<ImmutableExample>(potentiallyMutatable);
            this.unmodifiable = Collections.unmodifiableSortedSet(iCouldEscapeAndBeModified);
        }

        public ImmutableExample first() {
            return unmodifiable.first();
        }
    }

    public final static class StoresCopiedCollectionAsObjectAndIterable {
        private final Iterable<ImmutableExample> unmodifiableReferencedAsIterable;

        public StoresCopiedCollectionAsObjectAndIterable(List<ImmutableExample> potentiallyMutatable) {
            this.unmodifiableReferencedAsIterable =
                    Collections.unmodifiableList(
                            new LinkedList<ImmutableExample>(potentiallyMutatable));
        }

        public ImmutableExample first() {
            return ((List<ImmutableExample>)unmodifiableReferencedAsIterable).get(0);
        }
    }

    public final static class ListFieldFromUnmodifiableArrayAsList {
        private final List<ImmutableExample> listOfImmutableThings;
        public ListFieldFromUnmodifiableArrayAsList(ImmutableExample[] potentiallyMutatable) {
            this.listOfImmutableThings = Collections.unmodifiableList(Arrays.asList(potentiallyMutatable));
        }

        public List<ImmutableExample> safelyReturned() {
            return listOfImmutableThings;
        }
    }

    public final static class SafelyCopiedListGenericOnMutableType {
        private final List<Date> unmodifiableList;

        public SafelyCopiedListGenericOnMutableType(List<Date> listOfMutatableType) {
            this.unmodifiableList = Collections.unmodifiableList(new ArrayList<Date>(listOfMutatableType));
        }

        public List<Date> safelyReturned() {
            return unmodifiableList;
        }
    }

    public final static class SafelyCopiedMapGenericOnMutableTypeForKey {
        private final Map<Date, ImmutableExample> unmodifiableMap;

        public SafelyCopiedMapGenericOnMutableTypeForKey(Map<Date, ImmutableExample> listOfMutatableType) {
            this.unmodifiableMap =
                    Collections.unmodifiableMap(new HashMap<Date, ImmutableExample>(listOfMutatableType));
        }
    }

    public final static class NestedGenericTypes {
        private final Map<List<Set<Map<String, List<Set<Date>>>>>, Set<Date>> unmodifiableMap;

        public NestedGenericTypes(Map<List<Set<Map<String, List<Set<Date>>>>>, Set<Date>> listOfMutatableType) {
            this.unmodifiableMap =
                    Collections.unmodifiableMap(new HashMap<List<Set<Map<String, List<Set<Date>>>>>, Set<Date>>(listOfMutatableType));
        }
    }

    public final static class UnsafelyCopyingMapIsNotSuppressedByAllowingMutableElementTypes {
        private final Map<Date, ImmutableExample> unmodifiableMap;

        public UnsafelyCopyingMapIsNotSuppressedByAllowingMutableElementTypes(Map<Date, ImmutableExample> mapOfMutatableType) {
            this.unmodifiableMap = Collections.unmodifiableMap(mapOfMutatableType);
        }
    }

    public final static class SafelyCopiedMapGenericOnImmutableTypeForKey_ManyFields {
        private final ImmutableExample field1;
        private final Map<ImmutableExample, ImmutableExample> unmodifiableMap;
        private final ImmutableExample field2;
        private final List<ImmutableExample> unmodifiableList;

        public SafelyCopiedMapGenericOnImmutableTypeForKey_ManyFields(
                ImmutableExample field1,
                Map<ImmutableExample, ImmutableExample> mapOfImmutableType,
                ImmutableExample field2,
                List<ImmutableExample> listOfImmutableType) {
            this.field1 = field1;
            this.unmodifiableMap = Collections.unmodifiableMap(new HashMap<ImmutableExample, ImmutableExample>(mapOfImmutableType));
            this.field2 = field2;
            this.unmodifiableList = Collections.unmodifiableList(new ArrayList<ImmutableExample>(listOfImmutableType));
        }
    }


    public final static class SafelyCopiedMap_UsesGenericTypeOfClass<SOME_GENERIC_TYPE> {
        private final Map<ImmutableExample, SOME_GENERIC_TYPE> unmodifiableMap;

        public SafelyCopiedMap_UsesGenericTypeOfClass(Map<ImmutableExample, SOME_GENERIC_TYPE> mapOfImmutableType) {
            this.unmodifiableMap = Collections.unmodifiableMap(new HashMap<ImmutableExample, SOME_GENERIC_TYPE>(mapOfImmutableType));
        }
    }

    public final static class CollectionWithByteArrayGenericType {
        private final Collection<byte[]> byteArrayCollection = new ArrayList<>();
    }

    public final static class CollectionWithStringArrayGenericType {
        private final Collection<String[]> stringArrayCollection = new ArrayList<>();
    }

    public interface ImmutableContainer<E> {
        E get();
    }

    public final static class HasImmutableContainerOfImmutableType {
        private final ImmutableContainer<ImmutableExample> field;
        public HasImmutableContainerOfImmutableType(ImmutableContainer<ImmutableExample> field) {
            this.field = field;
        }
    }
    public final static class HasImmutableContainerOfMutableType {
        private final ImmutableContainer<Date> field;
        public HasImmutableContainerOfMutableType(ImmutableContainer<Date> field) {
            this.field = field;
        }
    }
    public final static class HasImmutableContainerOfGenericType<T> {
        private final ImmutableContainer<T> field;
        public HasImmutableContainerOfGenericType(ImmutableContainer<T> field) {
            this.field = field;
        }
    }
}
