package org.mutabilitydetector;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.mutabilitydetector.unittesting.MutabilityAssertionError;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
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
            assertThat(e.getMessage(), is("\n"+
                    "Expected: org.mutabilitydetector.ErrorLocationTest$ClassWithArrayField to be IMMUTABLE\n" +
                    "     but: org.mutabilitydetector.ErrorLocationTest$ClassWithArrayField is actually NOT_IMMUTABLE\n" +
                    "    Reasons:\n" +
                    "        Field is an array. [Field: arrayField, Class: org.mutabilitydetector.ErrorLocationTest$ClassWithArrayField]\n" +
                    "    Allowed reasons:\n" +
                    "        None."));
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
            assertThat(e.getMessage(), is("\n"+
                    "Expected: org.mutabilitydetector.ErrorLocationTest$ClassWithCollectionWithMutableElementTypeToField to be IMMUTABLE\n" +
                    "     but: org.mutabilitydetector.ErrorLocationTest$ClassWithCollectionWithMutableElementTypeToField is actually NOT_IMMUTABLE\n" +
                    "    Reasons:\n" +
                    "        Field can have a mutable type (java.util.ArrayList) assigned to it. [Field: collectionWithMutableType, Class: org.mutabilitydetector.ErrorLocationTest$ClassWithCollectionWithMutableElementTypeToField]\n" +
                    "        Field can have collection with mutable element type (java.util.List<org.mutabilitydetector.ErrorLocationTest$MutableClass>) assigned to it. [Field: collectionWithMutableType, Class: org.mutabilitydetector.ErrorLocationTest$ClassWithCollectionWithMutableElementTypeToField]\n" +
                    "    Allowed reasons:\n" +
                    "        None."));
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // CanSubclassChecker: code location points to the class (correct).
    // Potential improvements: Class declaration line with access modifier, etc. List of available subclasses if there are already any?
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class NonFinalClass {
    }

    @Test
    public void isImmutableCanSubclass() throws Exception {
        try {
            assertImmutable(NonFinalClass.class);
        } catch (MutabilityAssertionError e) {
            assertThat(e.getMessage(), is("\n"+
                    "Expected: org.mutabilitydetector.ErrorLocationTest$NonFinalClass to be IMMUTABLE\n" +
                    "     but: org.mutabilitydetector.ErrorLocationTest$NonFinalClass is actually NOT_IMMUTABLE\n" +
                    "    Reasons:\n" +
                    "        Can be subclassed, therefore parameters declared to be this type could be mutable subclasses at runtime. [Class: org.mutabilitydetector.ErrorLocationTest$NonFinalClass]\n" +
                    "    Allowed reasons:\n" +
                    "        None."));
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
            assertThat(e.getMessage(), is("\n"+
                    "Expected: org.mutabilitydetector.ErrorLocationTest$AbstractClass to be IMMUTABLE\n" +
                    "     but: org.mutabilitydetector.ErrorLocationTest$AbstractClass is actually NOT_IMMUTABLE\n" +
                    "    Reasons:\n" +
                    "        Can be subclassed, therefore parameters declared to be this type could be mutable subclasses at runtime. [Class: org.mutabilitydetector.ErrorLocationTest$AbstractClass]\n" +
                    "        Is inherently mutable, as declared as an abstract type. [Class: org.mutabilitydetector.ErrorLocationTest$AbstractClass]\n" +
                    "    Allowed reasons:\n" +
                    "        None."));
        }
    }

    @Test
    public void isImmutableInherentlyMutable_Interface() throws Exception {
        try {
            assertImmutable(AnInterface.class);
        } catch (MutabilityAssertionError e) {
            assertThat(e.getMessage(), is("\n"+
                    "Expected: org.mutabilitydetector.ErrorLocationTest$AnInterface to be IMMUTABLE\n" +
                    "     but: org.mutabilitydetector.ErrorLocationTest$AnInterface is actually NOT_IMMUTABLE\n" +
                    "    Reasons:\n" +
                    "        Is inherently mutable, as declared as an abstract type. [Class: org.mutabilitydetector.ErrorLocationTest$AnInterface]\n" +
                    "    Allowed reasons:\n" +
                    "        None."));
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
            assertThat(e.getMessage(), is("\n"+
                    "Expected: org.mutabilitydetector.ErrorLocationTest$ClassWithNonFinalField to be IMMUTABLE\n" +
                    "     but: org.mutabilitydetector.ErrorLocationTest$ClassWithNonFinalField is actually EFFECTIVELY_IMMUTABLE\n" +
                    "    Reasons:\n" +
                    "        Field is not final, if shared across threads the Java Memory Model will not guarantee it is initialised before it is read. [Field: publicField, Class: org.mutabilitydetector.ErrorLocationTest$ClassWithNonFinalField]\n" +
                    "    Allowed reasons:\n" +
                    "        None."));
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
            assertThat(e.getMessage(), is("\n"+
                    "Expected: org.mutabilitydetector.ErrorLocationTest$ClassWithPublicNonFinalField to be IMMUTABLE\n" +
                    "     but: org.mutabilitydetector.ErrorLocationTest$ClassWithPublicNonFinalField is actually NOT_IMMUTABLE\n" +
                    "    Reasons:\n" +
                    "        Field is visible outwith this class, and is not declared final. [Field: publicField, Class: org.mutabilitydetector.ErrorLocationTest$ClassWithPublicNonFinalField]\n" +
                    "        Field is not final, if shared across threads the Java Memory Model will not guarantee it is initialised before it is read. [Field: publicField, Class: org.mutabilitydetector.ErrorLocationTest$ClassWithPublicNonFinalField]\n" +
                    "    Allowed reasons:\n" +
                    "        None."));
        }
    }

    @Test
    public void isImmutablePublishedNonFinalField_Protected() throws Exception {
        try {
            assertImmutable(ClassWithPublicNonFinalField.class);
        } catch (MutabilityAssertionError e) {
            assertThat(e.getMessage(), is("\n"+
                    "Expected: org.mutabilitydetector.ErrorLocationTest$ClassWithPublicNonFinalField to be IMMUTABLE\n" +
                    "     but: org.mutabilitydetector.ErrorLocationTest$ClassWithPublicNonFinalField is actually NOT_IMMUTABLE\n" +
                    "    Reasons:\n" +
                    "        Field is visible outwith this class, and is not declared final. [Field: publicField, Class: org.mutabilitydetector.ErrorLocationTest$ClassWithPublicNonFinalField]\n" +
                    "        Field is not final, if shared across threads the Java Memory Model will not guarantee it is initialised before it is read. [Field: publicField, Class: org.mutabilitydetector.ErrorLocationTest$ClassWithPublicNonFinalField]\n" +
                    "    Allowed reasons:\n" +
                    "        None."));
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
            assertThat(e.getMessage(), is("\n"+
                    "Expected: org.mutabilitydetector.ErrorLocationTest$ClassWithSetterMethod to be IMMUTABLE\n" +
                    "     but: org.mutabilitydetector.ErrorLocationTest$ClassWithSetterMethod is actually NOT_IMMUTABLE\n" +
                    "    Reasons:\n" +
                    "        Field is not final, if shared across threads the Java Memory Model will not guarantee it is initialised before it is read. [Field: field, Class: org.mutabilitydetector.ErrorLocationTest$ClassWithSetterMethod]\n" +
                    "        Field [field] can be reassigned within method [setField] [Field: field, Class: org.mutabilitydetector.ErrorLocationTest$ClassWithSetterMethod]\n" +
                    "    Allowed reasons:\n" +
                    "        None."));
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // EscapedThisReferenceChecker: code location points to the class passing the this reference (correct).
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
            assertThat(e.getMessage(), is("\n"+
                    "Expected: org.mutabilitydetector.ErrorLocationTest$ClassPassingThisReference to be IMMUTABLE\n" +
                    "     but: org.mutabilitydetector.ErrorLocationTest$ClassPassingThisReference is actually NOT_IMMUTABLE\n" +
                    "    Reasons:\n" +
                    "        Field can have a mutable type (org.mutabilitydetector.ErrorLocationTest) assigned to it. [Field: this$0, Class: org.mutabilitydetector.ErrorLocationTest$ClassPassingThisReference]\n" +
                    "        Field can have a mutable type (org.mutabilitydetector.ErrorLocationTest$ClassWithThisReference) assigned to it. [Field: field, Class: org.mutabilitydetector.ErrorLocationTest$ClassPassingThisReference]\n" +
                    "        The 'this' reference is passed outwith the constructor. [Class: org.mutabilitydetector.ErrorLocationTest$ClassPassingThisReference]\n" +
                    "    Allowed reasons:\n" +
                    "        None."));
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // MutableTypeToFieldChecker:
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Cyclic dependency between 2 classes: wrong reasons?? Cyclic dependency is not mentioned!

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
            assertThat(e.getMessage(), is("\n"+
                    "Expected: org.mutabilitydetector.ErrorLocationTest$CyclicDependencyClassB to be IMMUTABLE\n" +
                    "     but: org.mutabilitydetector.ErrorLocationTest$CyclicDependencyClassB is actually NOT_IMMUTABLE\n" +
                    "    Reasons:\n" +
                    "        Field can have a mutable type (org.mutabilitydetector.ErrorLocationTest$CyclicDependencyClassA) assigned to it. [Field: classAField, Class: org.mutabilitydetector.ErrorLocationTest$CyclicDependencyClassB]\n" +
                    "        The 'this' reference is passed outwith the constructor. [Class: org.mutabilitydetector.ErrorLocationTest$CyclicDependencyClassB]\n" +
                    "    Allowed reasons:\n" +
                    "        None."));

        }
    }

    // Own Cyclic dependency: code location points to field (correct)
    // Potential Improvements: Line number

    public final static class OwnCyclicDependencyClass {
        private final OwnCyclicDependencyClass field;


        public OwnCyclicDependencyClass(OwnCyclicDependencyClass field) {
            this.field = field;
        }
    }

    @Test
    public void isImmutableMutableTypeToField_OwnCyclicDependency() throws Exception {
        try {
            assertImmutable(OwnCyclicDependencyClass.class);
        } catch (MutabilityAssertionError e) {
            assertThat(e.getMessage(), is("\n"+
                    "Expected: org.mutabilitydetector.ErrorLocationTest$OwnCyclicDependencyClass to be IMMUTABLE\n" +
                    "     but: org.mutabilitydetector.ErrorLocationTest$OwnCyclicDependencyClass is actually NOT_IMMUTABLE\n" +
                    "    Reasons:\n" +
                    "        There is a field assigned which creates a circular reference. [Field: field, Class: org.mutabilitydetector.ErrorLocationTest$OwnCyclicDependencyClass]\n" +
                    "    Allowed reasons:\n" +
                    "        None."));
        }
    }

    // Mutable Field: code location points to the field. Mutable type is output (correct)
    // Potential improvements: Line number

    public final static class ClassWithMutableField {
        private final MutableClass field;

        public ClassWithMutableField(MutableClass field) {
            this.field = field;
        }
    }

    @Test
    public void isImmutableMutableTypeToField_ClassWithMutableField() throws Exception {
        try {
            assertImmutable(ClassWithMutableField.class);
        } catch (MutabilityAssertionError e) {
            assertThat(e.getMessage(), is("\n"+
                    "Expected: org.mutabilitydetector.ErrorLocationTest$ClassWithMutableField to be IMMUTABLE\n" +
                    "     but: org.mutabilitydetector.ErrorLocationTest$ClassWithMutableField is actually NOT_IMMUTABLE\n" +
                    "    Reasons:\n" +
                    "        Field can have a mutable type (org.mutabilitydetector.ErrorLocationTest$MutableClass) assigned to it. [Field: field, Class: org.mutabilitydetector.ErrorLocationTest$ClassWithMutableField]\n" +
                    "    Allowed reasons:\n" +
                    "        None."));
        }
    }

    // Abstract Field: code location points to the field. The abstract type is output.
    // Potential improvements: Line of code

    public final static class ClassWithAbstractField {
        private final AbstractClass field;

        public ClassWithAbstractField(AbstractClass field) {
            this.field = field;
        }
    }

    @Test
    public void isImmutableMutableTypeToField_ClassWithAbstractField() throws Exception {
        try {
            assertImmutable(ClassWithAbstractField.class);
        } catch (MutabilityAssertionError e) {
            assertThat(e.getMessage(), is("\n"+
                    "Expected: org.mutabilitydetector.ErrorLocationTest$ClassWithAbstractField to be IMMUTABLE\n" +
                    "     but: org.mutabilitydetector.ErrorLocationTest$ClassWithAbstractField is actually NOT_IMMUTABLE\n" +
                    "    Reasons:\n" +
                    "        Field can have an abstract type (org.mutabilitydetector.ErrorLocationTest$AbstractClass) assigned to it. [Field: field, Class: org.mutabilitydetector.ErrorLocationTest$ClassWithAbstractField]\n" +
                    "    Allowed reasons:\n" +
                    "        None."));
        }
    }

    // CollectionWrappingWithoutCopy: code location points to the field. Wouldn't it be better to display assignment without copy? Or the line where the copy should be done?

    public final static class ClassWrappingCollectionWithoutCopy {
        private final Collection<String> collection;

        public ClassWrappingCollectionWithoutCopy(Collection<String> collection) {
            this.collection = Collections.unmodifiableCollection(collection);
        }
    }

    @Test
    public void isImmutableMutableTypeToField_ClassWrappingCollectionWithoutCopy() throws Exception {
        try {
            assertImmutable(ClassWrappingCollectionWithoutCopy.class);
        } catch (MutabilityAssertionError e) {
            assertThat(e.getMessage(), is("\n"+
                    "Expected: org.mutabilitydetector.ErrorLocationTest$ClassWrappingCollectionWithoutCopy to be IMMUTABLE\n" +
                    "     but: org.mutabilitydetector.ErrorLocationTest$ClassWrappingCollectionWithoutCopy is actually NOT_IMMUTABLE\n" +
                    "    Reasons:\n" +
                    "        Attempts to wrap mutable collection type without safely performing a copy first. [Field: collection, Class: org.mutabilitydetector.ErrorLocationTest$ClassWrappingCollectionWithoutCopy]\n" +
                    "    Allowed reasons:\n" +
                    "        None."));
        }
    }

    // ClassNotWrappingCollection: weird error message. Nothing is being wrapped but the error message still mentions usage of non-whitelisted wrapper method.

    public final static class ClassNotWrappingCollection {
        private final Collection<String> collection;

        public ClassNotWrappingCollection(Collection<String> collection) {
            this.collection = collection;
        }
    }

    @Test
    public void isImmutableMutableTypeToField_ClassNotWrappingCollection() throws Exception {
        try {
            assertImmutable(ClassNotWrappingCollection.class);
        } catch (MutabilityAssertionError e) {
            assertThat(e.getMessage(), is("\n"+
                    "Expected: org.mutabilitydetector.ErrorLocationTest$ClassNotWrappingCollection to be IMMUTABLE\n" +
                    "     but: org.mutabilitydetector.ErrorLocationTest$ClassNotWrappingCollection is actually NOT_IMMUTABLE\n" +
                    "    Reasons:\n" +
                    "        Attempts to wrap mutable collection type using a non-whitelisted unmodifiable wrapper method. [Field: collection, Class: org.mutabilitydetector.ErrorLocationTest$ClassNotWrappingCollection]\n" +
                    "    Allowed reasons:\n" +
                    "        None."));
        }
    }

    // ClassNotWrappingCollection: weird error message. Nothing is being wrapped but the error message still mentions usage of non-whitelisted wrapper method.
    // Error message changes to "Field can have an abstract type" if private copy method is inlined.

    public final static class ClassWrappingWithNonWhitelistedMethod {
        private final Collection<String> collection;

        public ClassWrappingWithNonWhitelistedMethod(Collection<String> collection) {
            this.collection = copy(collection);
        }

        private Collection<String> copy(Collection<String> collection) {
            return ImmutableList.copyOf(collection);
        }
    }

    @Test
    public void isImmutableMutableTypeToField_ClassWrappingWithNonWhitelistedMethod() throws Exception {
        try {
            assertImmutable(ClassWrappingWithNonWhitelistedMethod.class);
        } catch (MutabilityAssertionError e) {
            assertThat(e.getMessage(), is("\n"+
                    "Expected: org.mutabilitydetector.ErrorLocationTest$ClassWrappingWithNonWhitelistedMethod to be IMMUTABLE\n" +
                    "     but: org.mutabilitydetector.ErrorLocationTest$ClassWrappingWithNonWhitelistedMethod is actually NOT_IMMUTABLE\n" +
                    "    Reasons:\n" +
                    "        Attempts to wrap mutable collection type using a non-whitelisted unmodifiable wrapper method. [Field: collection, Class: org.mutabilitydetector.ErrorLocationTest$ClassWrappingWithNonWhitelistedMethod]\n" +
                    "    Allowed reasons:\n" +
                    "        None."));
        }
    }

    // ClassWithFieldCanBeAssignedArray: code location points to field. Better would be to display the assignment line.

    public final static class ClassWithFieldCanBeAssignedArray {
        private final Object arrayField;


        public ClassWithFieldCanBeAssignedArray(int[] arrayField) {
            this.arrayField = arrayField;
        }
    }

    @Test
    public void ClassWithFieldCanBeAssignedArray() throws Exception {
        try {
            assertImmutable(ClassWithFieldCanBeAssignedArray.class);
        } catch (MutabilityAssertionError e) {
            assertThat(e.getMessage(), is("\n"+
                    "Expected: org.mutabilitydetector.ErrorLocationTest$ClassWithFieldCanBeAssignedArray to be IMMUTABLE\n" +
                    "     but: org.mutabilitydetector.ErrorLocationTest$ClassWithFieldCanBeAssignedArray is actually NOT_IMMUTABLE\n" +
                    "    Reasons:\n" +
                    "        Field can have a mutable type (an array) assigned to it. [Field: arrayField, Class: org.mutabilitydetector.ErrorLocationTest$ClassWithFieldCanBeAssignedArray]\n" +
                    "    Allowed reasons:\n" +
                    "        None."));
        }
    }

}
