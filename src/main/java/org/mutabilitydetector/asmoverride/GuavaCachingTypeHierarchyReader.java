/*
 *    Copyright (c) 2008-2013 Graham Allan
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
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
