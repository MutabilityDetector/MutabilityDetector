package org.mutabilitydetector.asmoverride;

import java.util.concurrent.ConcurrentMap;

import org.objectweb.asm.Type;

import com.google.common.collect.MapMaker;

public class IsAssignableFromCachingTypeHierarchyReader extends TypeHierarchyReader {

    private final ConcurrentMap<TypeAssignability, Boolean> isAssignableFromCache;
    private final TypeHierarchyReader baseReader;

    public IsAssignableFromCachingTypeHierarchyReader(TypeHierarchyReader baseReader) {
        this.baseReader = baseReader;
        this.isAssignableFromCache =  new MapMaker().makeMap();

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
    
    @Override
    public boolean isAssignableFrom(final Type t, final Type u) {
        TypeAssignability assignability = new TypeAssignability(t, u);
        if (!isAssignableFromCache.containsKey(assignability)) {
            isAssignableFromCache.put(assignability, baseReader.isAssignableFrom(t, u));
        }
        return isAssignableFromCache.get(assignability);
    }
    
    private static class TypeAssignability {
        public final Type toType, fromType;
        private int hashCode;
        
        public TypeAssignability(Type toType, Type fromType) {
            this.toType = toType;
            this.fromType = fromType;
            this.hashCode = calculateHashCode();
        }

        private int calculateHashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * fromType.hashCode();
            result = prime * toType.hashCode();
            return result;
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
