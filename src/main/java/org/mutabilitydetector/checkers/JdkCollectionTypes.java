package org.mutabilitydetector.checkers;

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



import org.mutabilitydetector.locations.Dotted;

import com.google.common.collect.ImmutableSet;

class JdkCollectionTypes {

    private static final ImmutableSet<String> JDK_COLLECTION_TYPES = ImmutableSet.<String>builder()
            .add("java.lang.Iterable")
            .add("java.util.Iterator")
            .add("java.util.List")
            .add("java.util.ArrayList")
            .add("java.util.LinkedList")
            .add("java.util.Vector")
            .add("java.util.concurrent.CopyOnWriteArrayList")
            .add("java.util.Set")
            .add("java.util.HashSet")
            .add("java.util.LinkedHashSet")
            .add("java.util.TreeSet")
            .add("java.util.TreeSet")
            .add("java.util.concurrent.ConcurrentSkipListSet")
            .add("java.util.concurrent.ConcurrentSkipListSet")
            .add("java.util.concurrent.CopyOnWriteArraySet")
            .add("java.util.Map")
            .add("java.util.HashMap")
            .add("java.util.IdentityHashMap")
            .add("java.util.TreeMap")
            .add("java.util.TreeMap")
            .add("java.util.WeakHashMap")
            .add("java.util.Hashtable")
            .add("java.util.IdentityHashMap")
            .add("java.util.LinkedHashMap")
            .add("java.util.concurrent.ConcurrentHashMap")
            .add("java.util.concurrent.ConcurrentSkipListMap")
            .add("java.util.concurrent.ConcurrentSkipListMap")
            .add("java.util.SortedMap")
            .add("java.util.TreeMap")
            .add("java.util.TreeMap")
            .add("java.util.concurrent.ConcurrentSkipListMap")
            .add("java.util.concurrent.ConcurrentSkipListMap")
            .add("java.util.SortedSet")
            .add("java.util.TreeSet")
            .add("java.util.TreeSet")
            .add("java.util.concurrent.ConcurrentSkipListSet")
            .add("java.util.concurrent.ConcurrentSkipListSet")
            .add("java.util.Collection")
            .add("java/util/ArrayList")
            .add("java/util/concurrent/CopyOnWriteArrayList")
            .add("java/util/LinkedList")
            .add("java/util/Vector")
            .add("java/util/HashSet")
            .add("java/util/LinkedHashSet")
            .add("java/util/TreeSet")
            .add("java/util/TreeSet")
            .add("java/util/concurrent/ConcurrentSkipListSet")
            .add("java/util/concurrent/ConcurrentSkipListSet")
            .add("java/util/concurrent/CopyOnWriteArraySet")
            .add("java/util/concurrent/ConcurrentLinkedQueue")
            .add("java/util/concurrent/DelayQueue")
            .add("java/util/concurrent/LinkedBlockingDeque")
            .add("java/util/concurrent/LinkedBlockingQueue")
            .add("java/util/concurrent/LinkedTransferQueue")
            .add("java/util/concurrent/PriorityBlockingQueue")
            .add("java/util/concurrent/PriorityQueue")
            .add("java/util/concurrent/PriorityQueue")
            .add("java/util/concurrent/PriorityQueue")
            .add("java/util/concurrent/ConcurrentLinkedDeque")
            .add("java/util/ArrayDeque")
            .add("java/util/concurrent/ArrayBlockingQueue")
            .add("java/util/Optional")
            .build();

    public boolean isCollectionType(Dotted type) {
        return JDK_COLLECTION_TYPES.contains(type.asString());
    }
}