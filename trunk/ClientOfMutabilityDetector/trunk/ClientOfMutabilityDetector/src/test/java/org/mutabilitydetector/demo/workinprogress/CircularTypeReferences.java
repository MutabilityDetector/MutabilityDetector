package org.mutabilitydetector.demo.workinprogress;

import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutable;

import org.junit.Ignore;
import org.junit.Test;

public class CircularTypeReferences {

    @Test @Ignore
    public void classWithACircularTypeReference() throws Exception {
        assertImmutable(HasCircularReference.class);
    }
    
    public static final class HasCircularReference {
        public final HasCircularReference h;
        public HasCircularReference(HasCircularReference other) {
            this.h = other;
        }
    }
}
