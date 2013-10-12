package org.mutabilitydetector;

import org.junit.Test;
import org.mutabilitydetector.unittesting.MutabilityAssertionError;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutable;


@SuppressWarnings("ALL")
public class ErrorLocationTest {

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ArrayFieldMutabilityChecker: code location points to the field (correct).
    // Potential Improvements: Line number. Whole declaration of the field with access modifier, type etc...
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public final static class ClassWithArrayField {
        private final int[] arrayField = null;
    }

    @Test
    public void isImmutableClassWithArrayField() throws Exception {
        try {
            assertImmutable(ClassWithArrayField.class);
        } catch (MutabilityAssertionError e) {
            assertEquals(e.getMessage(),
                    "\n" +
                            "Expected: org.mutabilitydetector.ErrorLocationTest$ClassWithArrayField to be IMMUTABLE\n" +
                            "     but: org.mutabilitydetector.ErrorLocationTest$ClassWithArrayField is actually NOT_IMMUTABLE\n" +
                            "    Reasons:\n" +
                            "        Field is an array. [Field: arrayField, Class: org.mutabilitydetector.ErrorLocationTest$ClassWithArrayField]\n" +
                            "    Allowed reasons:\n" +
                            "        None.");
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // CollectionWithMutableElementTypeToFieldChecker: code location points to the field, as well as the collection declaration with the mutable type (correct)
    // Potential improvements: Line number.
    // Other improvements: avoid multiple reasons for this case (ABSTRACT_COLLECTION_TYPE_TO_FIELD, COLLECTION_FIELD_WITH_MUTABLE_ELEMENT_TYPE)
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public final static class MutableClass {
        public String publicField;
    }

    public final static class ClassWithCollectionWithMutableElementTypeToField {
        private final List<MutableClass> collectionWithMutableType = new ArrayList<MutableClass>();
    }

    @Test
    public void isImmutableCollectionWithMutableElementTypeToField() throws Exception {
        try {
            assertImmutable(ClassWithCollectionWithMutableElementTypeToField.class);
        } catch (MutabilityAssertionError e) {
            assertEquals(e.getMessage(),
                    "\n" +
                            "Expected: org.mutabilitydetector.ErrorLocationTest$ClassWithCollectionWithMutableElementTypeToField to be IMMUTABLE\n" +
                            "     but: org.mutabilitydetector.ErrorLocationTest$ClassWithCollectionWithMutableElementTypeToField is actually NOT_IMMUTABLE\n" +
                            "    Reasons:\n" +
                            "        Field can have a mutable type (java.util.ArrayList) assigned to it. [Field: collectionWithMutableType, Class: org.mutabilitydetector.ErrorLocationTest$ClassWithCollectionWithMutableElementTypeToField]\n" +
                            "        Field can have collection with mutable element type (java.util.List<org.mutabilitydetector.ErrorLocationTest$MutableClass>) assigned to it. [Field: collectionWithMutableType, Class: org.mutabilitydetector.ErrorLocationTest$ClassWithCollectionWithMutableElementTypeToField]\n" +
                            "    Allowed reasons:\n" +
                            "        None.");
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // MutableTypeToFieldChecker:
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // TODO: finish up audit for MutableTypeToFieldChecker

    public final static class CyclicDependencyClassA {
        private final CyclicDependencyClassB classBField;

        public CyclicDependencyClassA(CyclicDependencyClassB classBField) {
            this.classBField = classBField;
        }
    }

    public final static class CyclicDependencyClassB {
        private final CyclicDependencyClassA classAField;

        public CyclicDependencyClassB() {
            this.classAField = new CyclicDependencyClassA(this);
        }
    }

    @Test
    public void isImmutableMutableTypeToField_CyclicDependency() throws Exception {
        try {
            assertImmutable(CyclicDependencyClassB.class);
        } catch (MutabilityAssertionError e) {
            assertEquals(e.getMessage(),
                    "\n" +
                            "Expected: org.mutabilitydetector.ErrorLocationTest$ClassWithCollectionWithMutableElementTypeToField to be IMMUTABLE\n" +
                            "     but: org.mutabilitydetector.ErrorLocationTest$ClassWithCollectionWithMutableElementTypeToField is actually NOT_IMMUTABLE\n" +
                            "    Reasons:\n" +
                            "        Field can have a mutable type (java.util.ArrayList) assigned to it. [Field: collectionWithMutableType, Class: org.mutabilitydetector.ErrorLocationTest$ClassWithCollectionWithMutableElementTypeToField]\n" +
                            "        Field can have collection with mutable element type (java.util.List<org.mutabilitydetector.ErrorLocationTest$MutableClass>) assigned to it. [Field: collectionWithMutableType, Class: org.mutabilitydetector.ErrorLocationTest$ClassWithCollectionWithMutableElementTypeToField]\n" +
                            "    Allowed reasons:\n" +
                            "        None.");
        }
    }



    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // CanSubclassChecker: code location points to the class (correct).
    // Potential improvements: Class declaration line with access modifier, etc. List of available subclasses if there are already any.
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class NonFinalClass {
    }

    @Test
    public void isImmutableCanSubclass() throws Exception {
        try {
            assertImmutable(NonFinalClass.class);
        } catch (MutabilityAssertionError e) {
            assertEquals(e.getMessage(),
                    "\n" +
                            "Expected: org.mutabilitydetector.ErrorLocationTest$NonFinalClass to be IMMUTABLE\n" +
                            "     but: org.mutabilitydetector.ErrorLocationTest$NonFinalClass is actually NOT_IMMUTABLE\n" +
                            "    Reasons:\n" +
                            "        Can be subclassed, therefore parameters declared to be this type could be mutable subclasses at runtime. [Class: org.mutabilitydetector.ErrorLocationTest$NonFinalClass]\n" +
                            "    Allowed reasons:\n" +
                            "        None.");
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // InherentTypeMutabilityChecker: code location points to the class (correct).
    // Other improvements: change output to "is declared as an interface" for interfaces. Avoid multiple reasons for abstract classes (ABSTRACT_TYPE_INHERENTLY_MUTABLE + CAN_BE_SUBCLASSED)
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public abstract static class AbstractClass {
    }

    public static interface AnInterface {

    }

    @Test
    public void isImmutableInherentlyMutable_Abstract() throws Exception {
        try {
            assertImmutable(AbstractClass.class);
        } catch (MutabilityAssertionError e) {
            assertEquals(e.getMessage(),
                    "\n" +
                            "Expected: org.mutabilitydetector.ErrorLocationTest$AbstractClass to be IMMUTABLE\n" +
                            "     but: org.mutabilitydetector.ErrorLocationTest$AbstractClass is actually NOT_IMMUTABLE\n" +
                            "    Reasons:\n" +
                            "        Can be subclassed, therefore parameters declared to be this type could be mutable subclasses at runtime. [Class: org.mutabilitydetector.ErrorLocationTest$AbstractClass]\n" +
                            "        Is inherently mutable, as declared as an abstract type. [Class: org.mutabilitydetector.ErrorLocationTest$AbstractClass]\n" +
                            "    Allowed reasons:\n" +
                            "        None.");
        }
    }

    @Test
    public void isImmutableInherentlyMutable_Interface() throws Exception {
        try {
            assertImmutable(AnInterface.class);
        } catch (MutabilityAssertionError e) {
            assertEquals(e.getMessage(),
                    "\n" +
                            "Expected: org.mutabilitydetector.ErrorLocationTest$AnInterface to be IMMUTABLE\n" +
                            "     but: org.mutabilitydetector.ErrorLocationTest$AnInterface is actually NOT_IMMUTABLE\n" +
                            "    Reasons:\n" +
                            "        Is inherently mutable, as declared as an abstract type. [Class: org.mutabilitydetector.ErrorLocationTest$AnInterface]\n" +
                            "    Allowed reasons:\n" +
                            "        None.");
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // NonFinalFieldChecker: code location points to the field (correct).
    // Potential improvements: Line number. Field declaration with access modifiers, etc...
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public final static class ClassWithNonFinalField {
        private String publicField;
    }


    @Test
    public void isImmutableNonFinalField() throws Exception {
        try {
            assertImmutable(ClassWithNonFinalField.class);
        } catch (MutabilityAssertionError e) {
            assertEquals(e.getMessage(),
                    "\n" +
                            "Expected: org.mutabilitydetector.ErrorLocationTest$ClassWithNonFinalField to be IMMUTABLE\n" +
                            "     but: org.mutabilitydetector.ErrorLocationTest$ClassWithNonFinalField is actually EFFECTIVELY_IMMUTABLE\n" +
                            "    Reasons:\n" +
                            "        Field is not final, if shared across threads the Java Memory Model will not guarantee it is initialised before it is read. [Field: publicField, Class: org.mutabilitydetector.ErrorLocationTest$ClassWithNonFinalField]\n" +
                            "    Allowed reasons:\n" +
                            "        None.");
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PublishedNonFinalFieldChecker: code location points to the field and class (correct).
    // Potential improvements: Line number. Field declaration with access modifiers, etc...
    // Other Improvements: avoid multiple reasons (PUBLISHED_NON_FINAL_FIELD,  NON_FINAL_FIELD)
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public final static class ClassWithPublicNonFinalField {
        public String publicField;
    }

    public final static class ClassWithProtectedNonFinalField {
        protected String publicField;
    }


    @Test
    public void isImmutablePublishedNonFinalField_Public() throws Exception {
        try {
            assertImmutable(ClassWithPublicNonFinalField.class);
        } catch (MutabilityAssertionError e) {
            assertEquals(e.getMessage(),
                    "\n" +
                            "Expected: org.mutabilitydetector.ErrorLocationTest$ClassWithPublicNonFinalField to be IMMUTABLE\n" +
                            "     but: org.mutabilitydetector.ErrorLocationTest$ClassWithPublicNonFinalField is actually NOT_IMMUTABLE\n" +
                            "    Reasons:\n" +
                            "        Field is visible outwith this class, and is not declared final. [Field: publicField, Class: org.mutabilitydetector.ErrorLocationTest$ClassWithPublicNonFinalField]\n" +
                            "        Field is not final, if shared across threads the Java Memory Model will not guarantee it is initialised before it is read. [Field: publicField, Class: org.mutabilitydetector.ErrorLocationTest$ClassWithPublicNonFinalField]\n" +
                            "    Allowed reasons:\n" +
                            "        None.");
        }
    }

    @Test
    public void isImmutablePublishedNonFinalField_Protected() throws Exception {
        try {
            assertImmutable(ClassWithPublicNonFinalField.class);
        } catch (MutabilityAssertionError e) {
            assertEquals(e.getMessage(),
                    "\n" +
                            "Expected: org.mutabilitydetector.ErrorLocationTest$ClassWithPublicNonFinalField to be IMMUTABLE\n" +
                            "     but: org.mutabilitydetector.ErrorLocationTest$ClassWithPublicNonFinalField is actually NOT_IMMUTABLE\n" +
                            "    Reasons:\n" +
                            "        Field is visible outwith this class, and is not declared final. [Field: publicField, Class: org.mutabilitydetector.ErrorLocationTest$ClassWithPublicNonFinalField]\n" +
                            "        Field is not final, if shared across threads the Java Memory Model will not guarantee it is initialised before it is read. [Field: publicField, Class: org.mutabilitydetector.ErrorLocationTest$ClassWithPublicNonFinalField]\n" +
                            "    Allowed reasons:\n" +
                            "        None.");
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // OldSetterMethodChecker: code location points to the field and class, name of the setter method is displayed. This is not wrong but actually just the name of the setter method and the class would be needed.
    // Potential improvements: Line number.
    // Other Improvements: avoid multiple reasons (FIELD_CAN_BE_REASSIGNED,  NON_FINAL_FIELD)
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public final static class ClassWithSetterMethod {
        private String field;

        public void setField(String field) {
            this.field = field;
        }
    }

    @Test
    public void isImmutableClassWithSetterMethod() throws Exception {
        try {
            assertImmutable(ClassWithSetterMethod.class);
        } catch (MutabilityAssertionError e) {
            assertEquals(e.getMessage(),
                    "\n" +
                            "Expected: org.mutabilitydetector.ErrorLocationTest$ClassWithSetterMethod to be IMMUTABLE\n" +
                            "     but: org.mutabilitydetector.ErrorLocationTest$ClassWithSetterMethod is actually NOT_IMMUTABLE\n" +
                            "    Reasons:\n" +
                            "        Field is not final, if shared across threads the Java Memory Model will not guarantee it is initialised before it is read. [Field: field, Class: org.mutabilitydetector.ErrorLocationTest$ClassWithSetterMethod]\n" +
                            "        Field [field] can be reassigned within method [setField] [Field: field, Class: org.mutabilitydetector.ErrorLocationTest$ClassWithSetterMethod]\n" +
                            "    Allowed reasons:\n" +
                            "        None.");
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // EscapedThisReferenceChecker: code location points to the class passing the this reference (ok).
    // Potential improvements: Line number where this reference is passed.
    // Other Improvements: avoid multiple reasons (FIELD_CAN_BE_REASSIGNED,  NON_FINAL_FIELD)
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public final class ClassWithThisReference {

        private final ClassPassingThisReference thisReference;

        public ClassWithThisReference(ClassPassingThisReference thisReference) {
            this.thisReference = thisReference;
        }
    }

    public final class ClassPassingThisReference {
        private final ClassWithThisReference field;

        public ClassPassingThisReference() {
            this.field = new ClassWithThisReference(this);
        }
    }


    @Test
    public void isImmutableClassPassingThisReference() throws Exception {
        try {
            assertImmutable(ClassPassingThisReference.class);
        } catch (MutabilityAssertionError e) {
            assertEquals(e.getMessage(),
                    "\n" +
                            "Expected: org.mutabilitydetector.ErrorLocationTest$ClassPassingThisReference to be IMMUTABLE\n" +
                            "     but: org.mutabilitydetector.ErrorLocationTest$ClassPassingThisReference is actually NOT_IMMUTABLE\n" +
                            "    Reasons:\n" +
                            "        Field can have a mutable type (org.mutabilitydetector.ErrorLocationTest) assigned to it. [Field: this$0, Class: org.mutabilitydetector.ErrorLocationTest$ClassPassingThisReference]\n" +
                            "        Field can have a mutable type (org.mutabilitydetector.ErrorLocationTest$ClassWithThisReference) assigned to it. [Field: field, Class: org.mutabilitydetector.ErrorLocationTest$ClassPassingThisReference]\n" +
                            "        The 'this' reference is passed outwith the constructor. [Class: org.mutabilitydetector.ErrorLocationTest$ClassPassingThisReference]\n" +
                            "    Allowed reasons:\n" +
                            "        None.");
        }
    }

}
