package org.mutabilitydetector.locations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mutabilitydetector.locations.ClassNameConverter.toDottedString;

import org.junit.Test;

public class ClassNameConverterTest {

    @Test
    public void canExtractTypeNameFromInternalDescriptorOfArrayReference() throws Exception {
        assertThat(toDottedString("[B"), is("[B"));
        assertThat(toDottedString("[[I"), is("[I"));
        assertThat(toDottedString("[[[[[[J"), is("[J"));
        assertThat(toDottedString("[Ljava/lang/Object;"), is("java.lang.Object"));
        assertThat(toDottedString("[[Ljava/lang/Object;"), is("java.lang.Object"));
    }

    @Test
    public void dottedClassNamesRemainTheSame() throws Exception {
        String dotted = "some.dotted.ClassName";
        assertEquals(dotted, toDottedString(dotted));
    }

    @Test
    public void slashedClassNameIsReturnedDotted() throws Exception {
        String slashed = "some/slashed/ClassName";
        assertEquals("some.slashed.ClassName", toDottedString(slashed));
    }

    @Test
    public void dotClassSuffixIsRemoved() throws Exception {
        String dotClass = "some/slashed/ClassName.class";
        assertEquals("some.slashed.ClassName", toDottedString(dotClass));
    }
    
    @Test
    public void doesNotReplaceTheWordClassIfItAppearsInMiddleOfWord() throws Exception {
        String containsWordClass = "some/slashed/packagewithwordclassinit/ClassName.class";
        assertEquals("some.slashed.packagewithwordclassinit.ClassName", toDottedString(containsWordClass));
    }

    @Test
    public void doesNotReplaceTheWordClassIfItAppearsAtStartOfPackageName() throws Exception {
        String containsWordClass = "com/sun/org/apache/bcel/internal/classfile/AccessFlags.class";
        assertEquals("com.sun.org.apache.bcel.internal.classfile.AccessFlags", toDottedString(containsWordClass));
    }
    
    @Test
    public void willStripAllArrayDescriptorsFromMultiDimensionalArrays() throws Exception {
        String multidimensionalPrimitiveArray = "[[[B";
        assertEquals("[B", toDottedString(multidimensionalPrimitiveArray));
    }
    
}
