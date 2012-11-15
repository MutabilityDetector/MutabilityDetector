package org.mutabilitydetector.demo.workinprogress;

import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutable;

import org.junit.Ignore;
import org.junit.Test;

public class TheStringProblem {

    @Test @Ignore
    public void java_lang_String() throws Exception {
        assertImmutable(String.class);
    }
    
}
