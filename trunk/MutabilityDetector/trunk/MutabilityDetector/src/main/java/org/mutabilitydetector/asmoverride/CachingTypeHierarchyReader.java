package org.mutabilitydetector.asmoverride;

import static com.google.common.collect.Maps.newConcurrentMap;

import java.util.concurrent.ConcurrentMap;

import org.objectweb.asm.Type;

public class CachingTypeHierarchyReader extends TypeHierarchyReader {

    private final TypeHierarchyReader baseReader;
    private final ConcurrentMap<Type, TypeHierarchy> typeHierarchyCache;
    private final ConcurrentMap<TypeAssignability, Boolean> isAssignableFromCache;
    public static long totalTimeSpent_isAssignableFrom = 0;
    public static long totalTimeSpent_isInterface = 0;
    public static long totalTimeSpent_getSuperclass = 0;
    public static long timesConstructed = 0;

    public CachingTypeHierarchyReader(TypeHierarchyReader baseReader) {
        this.baseReader = baseReader;
        this.typeHierarchyCache = newConcurrentMap();
        this.isAssignableFromCache = newConcurrentMap();
    }
    
    @Override
    public TypeHierarchy hierarchyOf(final Type t) {
        if (!typeHierarchyCache.containsKey(t)) {
            typeHierarchyCache.put(t, baseReader.hierarchyOf(t));
        }
        return typeHierarchyCache.get(t);
    }
    
    @Override
    public boolean isAssignableFrom(final Type t, final Type u) {
        TypeAssignability assignability = new TypeAssignability(t, u);
        if (!isAssignableFromCache.containsKey(assignability)) {
            isAssignableFromCache.put(assignability, super.isAssignableFrom(t, u));
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
