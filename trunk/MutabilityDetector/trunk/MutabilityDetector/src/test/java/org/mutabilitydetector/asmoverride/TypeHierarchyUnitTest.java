/***
 * ASM: a very small and fast Java bytecode manipulation framework
 * Copyright (c) 2000-2011 INRIA, France Telecom
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.mutabilitydetector.asmoverride;

import static org.mutabilitydetector.asmoverride.TypeHierarchyReader.TypeHierarchy.JAVA_LANG_OBJECT;
import static org.objectweb.asm.Type.getType;

import java.io.Serializable;

import junit.framework.TestCase;

import org.objectweb.asm.Type;

public class TypeHierarchyUnitTest extends TestCase {
    
    private TypeHierarchyReader typeHierarchyReader = new TypeHierarchyReader();
    
    public void testSuperClassOfConcreteClassExtendingObjectImplicitlyIsTypeRepresentingJavaLangObject() {
        assertEquals(JAVA_LANG_OBJECT.type(), typeHierarchyReader.getSuperClass(getType(UnrelatedType.class)));
    }

    public void testSuperClassOfSubclass() {
        assertEquals(getType(Superclass.class), typeHierarchyReader.getSuperClass(getType(Subclass.class)));
    }

    public void testSuperClassOfInterfaceWithNoSuperInterfaceIsObject() {
        assertEquals(JAVA_LANG_OBJECT.type(), typeHierarchyReader.getSuperClass(getType(Interface.class)));
    }

    public void testSuperClassOfSubInterfaceIsJavaLangObject() {
        assertEquals(JAVA_LANG_OBJECT.type(), typeHierarchyReader.getSuperClass(getType(SubInterface.class)));
    }
    
    public void testSuperclassOfArrayClassHasSameSemanticsAsJavaLangClass_getSuperClass() throws Exception {
        assertEquals(getType(Object[].class.getSuperclass()), typeHierarchyReader.getSuperClass(getType(Object[].class)));
        assertEquals(getType(Interface[].class.getSuperclass()), typeHierarchyReader.getSuperClass(getType(Interface[].class)));
        assertEquals(getType(Superclass[].class.getSuperclass()), typeHierarchyReader.getSuperClass(getType(Superclass[].class)));
    }
    
    public void testClassIsAssignableFromItself() {
        assertIsAssignableFrom(AssignableFromItself.class, AssignableFromItself.class);
    }

    public void testClassIsNotAssignableToUnrelatedType() {
        assertIsNotAssignableFrom(AssignableFromItself.class, UnrelatedType.class);
        assertIsNotAssignableFrom(UnrelatedType.class, AssignableFromItself.class);
    }
    
    public void testSuperclassIsAssignableFromSubclass() throws Exception {
        assertIsAssignableFrom(Superclass.class, Subclass.class);
    }
    
    public void testIndirectSubclassIsAssignableToSuperclass() throws Exception {
        assertIsAssignableFrom(Superclass.class, SubSubclass.class);
    }
    
    public void testSubclassIsNotAssignableToOtherClassWithSameSuperclass() throws Exception {
        assertIsNotAssignableFrom(Subclass.class, OtherSubclass.class);
    }

    public void testSubclassIsNotAssignableFromSuperclass() throws Exception {
        assertIsNotAssignableFrom(Subclass.class, Superclass.class);
    }
    
    public void testInterfaceIsAssignableFromImplementingClass() throws Exception {
        assertIsAssignableFrom(Interface.class, ImplementsInterface.class);
    }
    
    public void testInterfaceIsAssignableFromSubclassOfImplementingClass() throws Exception {
        assertIsAssignableFrom(Interface.class, ExtendsImplementsInterface.class);
        assertIsNotAssignableFrom(ExtendsImplementsInterface.class, Interface.class);
    }
    
    public void testSuperInterfaceIsAssignableFromSubInterface() throws Exception {
        assertIsAssignableFrom(SuperInterface.class, SubInterface.class);
        assertIsNotAssignableFrom(SubInterface.class, SuperInterface.class);
    }
    
    public void testImplementingClassIsNotAssignableFromInterface() throws Exception {
        assertIsNotAssignableFrom(ImplementsInterface.class, Interface.class);
    }
    
    public void testObjectIsAssignableFromAnything() throws Exception {
        assertIsAssignableFrom(Object.class, Superclass.class);
        assertIsAssignableFrom(Object.class, Subclass.class);
        assertIsAssignableFrom(Object.class, Interface.class);
        assertIsAssignableFrom(Object.class, SubInterface.class);
    }
    
    public void testAllImplementedInterfacesAreAssignableFromImplementingClass() throws Exception {
        assertIsAssignableFrom(Interface.class, ImplementsSeveralInterfaces.class);
        assertIsAssignableFrom(SubInterface.class, ImplementsSeveralInterfaces.class);
        assertIsAssignableFrom(SuperInterface.class, ImplementsSeveralInterfaces.class);
        assertIsNotAssignableFrom(OtherImplementsInterface.class, ImplementsSeveralInterfaces.class);
    }
    
    public void testInterfaceIsAssignableFromClassWithSuperclassOutwithInterfaceHierarchy() throws Exception {
        assertIsAssignableFrom(SuperInterface.class, ExtendsClassOutwithInterfaceHierarchy.class);
    }
    
    public void testArrayTypeAssignment() throws Exception {
        assertIsAssignableFrom(Object.class, Interface[].class);
        assertIsAssignableFrom(Cloneable.class, Interface[].class);
        assertIsAssignableFrom(Serializable.class, Interface[].class);
        assertIsAssignableFrom(Object[].class, Interface[].class);
        assertIsAssignableFrom(Object[].class, Interface[].class);
        assertIsAssignableFrom(Interface[].class, Interface[].class);
        assertIsAssignableFrom(Interface[].class, ImplementsInterface[].class);
        assertIsNotAssignableFrom(ImplementsInterface[].class, Interface[].class);
        assertIsAssignableFrom(Interface[].class, ExtendsImplementsInterface[].class);
        assertIsAssignableFrom(Object[].class, Superclass[].class);
        assertIsAssignableFrom(Object[].class, Subclass[].class);
        assertIsAssignableFrom(Superclass[].class, Subclass[].class);
        assertIsNotAssignableFrom(Subclass[].class, Superclass[].class);
    }
    
    public void testArrayDimensionAssignment() throws Exception {
        assertIsAssignableFrom(Object.class, Object[].class);
        assertIsNotAssignableFrom(Object[].class, Object.class);
        assertIsAssignableFrom(Object.class, Interface[].class);
        assertIsAssignableFrom(Object[].class, Interface[][].class);
        assertIsAssignableFrom(Object[][].class, Interface[][].class);
        assertIsNotAssignableFrom(Interface[].class, Interface.class);
        assertIsNotAssignableFrom(Interface[].class, Interface[][].class);
        assertIsNotAssignableFrom(Interface[][].class, Interface[].class);
    }
    
    public void testAnonymousInnerClasses() throws Exception {
        assertIsAssignableFrom(Interface.class, new Interface() { }.getClass());
        assertIsNotAssignableFrom(new Interface() { }.getClass(), Interface.class);
    }

    public void testAssignmentOfPrimitiveTypes() throws Exception {
        assertIsAssignableFrom(boolean.class, boolean.class);
        assertIsAssignableFrom(byte.class, byte.class);
        assertIsAssignableFrom(char.class, char.class);
        assertIsAssignableFrom(short.class, short.class);
        assertIsAssignableFrom(int.class, int.class);
        assertIsAssignableFrom(long.class, long.class);
        assertIsAssignableFrom(float.class, float.class);
        assertIsAssignableFrom(double.class, double.class);
        assertIsAssignableFrom(void.class, void.class);
    }

    public void testAssignmentOfPrimitiveArrayTypes() throws Exception {
        assertIsAssignableFrom(boolean[].class, boolean[].class);
        assertIsAssignableFrom(byte[].class, byte[].class);
        assertIsAssignableFrom(char[].class, char[].class);
        assertIsAssignableFrom(short[].class, short[].class);
        assertIsAssignableFrom(int[].class, int[].class);
        assertIsAssignableFrom(long[].class, long[].class);
        assertIsAssignableFrom(float[].class, float[].class);
        assertIsAssignableFrom(double[].class, double[].class);
    }
    
    public void testGetCommonSuperClass_shouldBeObjectForUnrelatedClasses() throws Exception {
        assertCommonSuperclass(Object.class, Superclass.class, UnrelatedType.class);
    }

    public void testGetCommonSuperClass_shouldBeClosestSharedSuperclass() throws Exception {
        assertCommonSuperclass(Superclass.class, Subclass.class, OtherSubclass.class);
    }

    public void testGetCommonSuperClass_shouldBeSameTypeWhenBothAreEqual() throws Exception {
        assertCommonSuperclass(UnrelatedType.class, UnrelatedType.class, UnrelatedType.class);
    }

    public void testGetCommonSuperClass_shouldBeSuperclassOfTwoGivenTypes() throws Exception {
        assertCommonSuperclass(Superclass.class, Superclass.class, Subclass.class);
    }

    public void testGetCommonSuperClass_shouldBeObjectForUnrelatedInterfaces() throws Exception {
        assertCommonSuperclass(Object.class, Interface.class, OtherInterface.class);
    }

    public void fails_returnsObject_testGetCommonSuperClass_shouldBeClosestSharedInterface() throws Exception {
        assertCommonSuperclass(SubInterface.class, ImplementsSeveralInterfaces.class, AlsoImplementsSubInterface.class);
    }

    private void assertIsAssignableFrom(Class<?> to, Class<?> from) {
        assertTrue("Assertion is not consistent with Class.isAssignableFrom", to.isAssignableFrom(from));
        Type toType = Type.getType(to);
        Type fromType = Type.getType(from);
        assertTrue("Type Hierarchy visitor is not consistent with Class.isAssignableFrom", 
                typeHierarchyReader.isAssignableFrom(toType, fromType));
    }

    private void assertIsNotAssignableFrom(Class<?> to, Class<?> from) {
        assertFalse("Assertion is not consistent with Class.isAssignableFrom", to.isAssignableFrom(from));
        Type toType = Type.getType(to);
        Type fromType = Type.getType(from);
        assertFalse("Type Hierarchy visitor is not consistent with Class.isAssignableFrom", 
                typeHierarchyReader.isAssignableFrom(toType, fromType));
    }
    
    private void assertCommonSuperclass(Class<?> expected, Class<?> first, Class<?> second) {
        assertEquals(slashedName(expected),
                typeHierarchyReader.getCommonSuperClass(slashedName(first), slashedName(second)));
        assertEquals(slashedName(expected),
                typeHierarchyReader.getCommonSuperClass(slashedName(second), slashedName(first)));
    }

    private String slashedName(Class<?> cls) {
        return cls.getName().replace(".", "/");
    }
    
    public static class AssignableFromItself { }
    
    public static class UnrelatedType { }
    
    public static class Superclass { }
    public static class Subclass extends Superclass { }
    public static class OtherSubclass extends Superclass { }
    public static class SubSubclass extends Subclass { }
    
    public static interface Interface { }
    public static interface OtherInterface { }
    public static class ImplementsInterface implements Interface { }
    public static class ExtendsImplementsInterface extends ImplementsInterface { }
    
    public static interface SuperInterface { }
    public static interface SubInterface extends SuperInterface { }
    public static interface OtherSubInterface { }
    
    public static class ImplementsSeveralInterfaces implements Interface, SubInterface { }
    public static class OtherImplementsInterface implements Interface { }
    public static class AlsoImplementsSubInterface implements SubInterface { }
    
    public static class ExtendsClassOutwithInterfaceHierarchy extends UnrelatedType implements SubInterface { }
    
}
