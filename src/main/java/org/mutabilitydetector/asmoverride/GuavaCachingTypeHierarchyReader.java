package org.mutabilitydetector.asmoverride;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.analysis.TypeHierarchyReader;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class GuavaCachingTypeHierarchyReader extends TypeHierarchyReader {

    private final LoadingCache<Type, TypeHierarchy> typeHierarchyCache;

    public GuavaCachingTypeHierarchyReader(TypeHierarchyReader baseReader, int initialCacheSize) {
        this.typeHierarchyCache = CacheBuilder.newBuilder().initialCapacity(initialCacheSize).build(typeHierarchyLoader(baseReader));
    }
    
    private static CacheLoader<Type, TypeHierarchy> typeHierarchyLoader(final TypeHierarchyReader baseReader) {
        return new CacheLoader<Type, TypeHierarchy>() {
            @Override public TypeHierarchy load(Type key) throws Exception {
                return baseReader.hierarchyOf(key);
            }
        };
    }

    @Override
    public TypeHierarchy hierarchyOf(final Type t) {
        return typeHierarchyCache.getUnchecked(t);
    }
    
}
