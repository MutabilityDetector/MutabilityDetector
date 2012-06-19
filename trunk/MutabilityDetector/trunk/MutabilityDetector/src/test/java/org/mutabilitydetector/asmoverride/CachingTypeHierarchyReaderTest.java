package org.mutabilitydetector.asmoverride;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.mutabilitydetector.asmoverride.TypeHierarchyReader.TypeHierarchy;
import org.objectweb.asm.Type;


public class CachingTypeHierarchyReaderTest {

    private final TypeHierarchyReader baseReader = mock(TypeHierarchyReader.class);
    private final Type toType = Type.getType(List.class);
    private final Type fromType = Type.getType(ArrayList.class);
    private final CachingTypeHierarchyReader reader = new CachingTypeHierarchyReader(baseReader);

    @Test
    public void usesUnderlyingReaderToCalculateTypeHierarchy() throws Exception {
        TypeHierarchy fromTypeHierarchy = new TypeHierarchy(fromType, toType, Collections.<Type>emptyList(), false);
        TypeHierarchy toTypeHierarchy = new TypeHierarchy(toType, null, Collections.<Type>emptyList(), true);

        when(baseReader.hierarchyOf(fromType)).thenReturn(fromTypeHierarchy);
        when(baseReader.hierarchyOf(toType)).thenReturn(toTypeHierarchy);
        
        assertSame(reader.hierarchyOf(fromType), fromTypeHierarchy);
        assertSame(reader.hierarchyOf(toType), toTypeHierarchy);
    }

    @Test
    public void cachesReturnValueOfUnderlyingReader() throws Exception {
        TypeHierarchy fromTypeHierarchy = new TypeHierarchy(fromType, toType, Collections.<Type>emptyList(), false);
        
        when(baseReader.hierarchyOf(fromType)).thenReturn(fromTypeHierarchy);
        
        reader.hierarchyOf(fromType);
        reader.hierarchyOf(fromType);
        
        verify(baseReader, times(1)).hierarchyOf(fromType);
    }
    
    @Test
    public void usesUnderlyingReaderToCalculateIsInterface() throws Exception {
        TypeHierarchy anInterfaceType = new TypeHierarchy(toType, null, Collections.<Type>emptyList(), true);
        when(baseReader.hierarchyOf(toType)).thenReturn(anInterfaceType);

        assertTrue(reader.isInterface(toType));
    }
    
    @Test
    public void usesUnderlyingReaderToGetSuperClass() throws Exception {
        TypeHierarchy typeHierarchy = new TypeHierarchy(fromType, toType, Collections.<Type>emptyList(), true);
        when(baseReader.hierarchyOf(fromType)).thenReturn(typeHierarchy);
            
        assertEquals(toType, reader.getSuperClass(fromType));
    }
    
}
