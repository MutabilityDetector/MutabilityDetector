package org.mutabilitydetector.locations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

public class ClassNameConverterTest {

    private final ClassNameConvertor converter = new ClassNameConvertor();
    
    @Test
    public void canExtractTypeNameFromInternalDescriptorOfArrayReference() throws Exception {
        assertThat(converter.dotted("[B"), is("[B"));
        assertThat(converter.dotted("[[I"), is("[[I"));
        assertThat(converter.dotted("[Ljava/lang/Object;"), is("java.lang.Object"));
        assertThat(converter.dotted("[[Ljava/lang/Object;"), is("java.lang.Object"));
    }
    
}
