package org.mutabilitydetector.benchmarks;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * From StackOverflow question.
 * http://stackoverflow.com/questions/5635032/is-this-class-threadsafe
 */
public class ImmutablePossiblyThreadsafeClass<K, V> {

    private final Map<K, V> map;

    public ImmutablePossiblyThreadsafeClass(final Map<K, V> map) {
        this.map = new HashMap<K, V>();

        for (Entry<K, V> entry : map.entrySet()) {
            this.map.put(entry.getKey(), entry.getValue());
        }
    }

    public V get(K key) {
        return this.map.get(key);
    }
}
