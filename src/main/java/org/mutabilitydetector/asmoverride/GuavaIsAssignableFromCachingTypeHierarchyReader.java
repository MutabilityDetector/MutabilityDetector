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

import com.google.common.base.Objects;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class GuavaIsAssignableFromCachingTypeHierarchyReader extends TypeHierarchyReader {

    private final LoadingCache<TypeAssignability, Boolean> isAssignableFromCache;
    private final TypeHierarchyReader baseReader;

    public GuavaIsAssignableFromCachingTypeHierarchyReader(TypeHierarchyReader baseReader) {
        this.baseReader = baseReader;
        this.isAssignableFromCache = CacheBuilder.newBuilder().build(isAssignableFromLoader());
    }
    
    @Override
    public TypeHierarchy hierarchyOf(Type t) {
        return baseReader.hierarchyOf(t);
    }
    
    @Override
    public Type getSuperClass(Type t) {
        return baseReader.getSuperClass(t);
    }
    
    @Override
    public boolean isInterface(Type t) {
        return baseReader.isInterface(t);
    }
    
    private CacheLoader<TypeAssignability, Boolean> isAssignableFromLoader() {
        return new CacheLoader<TypeAssignability, Boolean>() {
            @Override public Boolean load(TypeAssignability key) throws Exception {
                return baseReader.isAssignableFrom(key.toType, key.fromType);
            } 
        };
    }

    @Override
    public boolean isAssignableFrom(final Type t, final Type u) {
        return isAssignableFromCache.getUnchecked(new TypeAssignability(t, u));
    }
    
    private static class TypeAssignability {
        public final Type toType, fromType;
        private final int hashCode;
        
        public TypeAssignability(Type toType, Type fromType) {
            this.toType = toType;
            this.fromType = fromType;
            this.hashCode = Objects.hashCode(toType, fromType);
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            } else if (obj == null) {
                return false;
            } else if (getClass() != obj.getClass()) {
                return false;
            }
            
            TypeAssignability other = (TypeAssignability) obj;
            return toType.equals(other.toType) && fromType.equals(other.fromType);
        }
    }
}
