package org.mutabilitydetector.asmoverride;

import java.util.concurrent.ConcurrentMap;

import org.objectweb.asm.Type;

import com.google.common.collect.MapMaker;

public class CachingTypeHierarchyReader extends TypeHierarchyReader {

    private final TypeHierarchyReader baseReader;
    private final ConcurrentMap<Type, TypeHierarchy> typeHierarchyCache;

    public CachingTypeHierarchyReader(TypeHierarchyReader baseReader, int initialCapacity) {
        this.baseReader = baseReader;
        this.typeHierarchyCache = new MapMaker().initialCapacity(initialCapacity).makeMap();
    }
    
    @Override
    public TypeHierarchy hierarchyOf(final Type t) {
        if (!typeHierarchyCache.containsKey(t)) {
            typeHierarchyCache.put(t, baseReader.hierarchyOf(t));
        }
        return typeHierarchyCache.get(t);
    }
}
