package org.mutabilitydetector;

import org.junit.Test;
import org.mutabilitydetector.unittesting.MutabilityAssertionError;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutable;

/**
 * @author: Marc Gomez
 */
@SuppressWarnings("ALL")
public class ErrorLocationTest {

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ArrayFieldMutabilityChecker: code location points to the field and class (correct).
    // Potential Improvements: Line number
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
    // CollectionWithMutableElementTypeToFieldChecker: code location points to the field and class, as well as the collection declaration with the mutable type (correct)
    // Potential improvements: line number.
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
    // MutableTypeToFieldChecker
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // CanSubclassChecker: code location points to the class (correct).
    // Potential improvements: lists of available subclasses if there are already any?
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
    // NonFinalFieldChecker: code location points to the field and class (correct).
    // Potential improvements: Line number.
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

    // PublishedNonFinalFieldChecker: code location points to the field and class (correct).
    // Potential improvements: Line number, current access modifier.
    // Other Improvements: avoid multiple reasons (PUBLISHED_NON_FINAL_FIELD,  NON_FINAL_FIELD)

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


}
