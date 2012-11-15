package org.mutabilitydetector.demo.workinprogress;

import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutable;

import org.junit.Ignore;
import org.junit.Test;

public class NaiveAliasAnalysis {
    
    @Test @Ignore
    public void escapedThisReference() throws Exception {
        assertImmutable(LetYourThisReferenceEscape.class);
    }

    @Test @Ignore
    public void sneakyEscapedThisReference() throws Exception {
        assertImmutable(SneakyLetYourThisReferenceEscape.class);
    }
    
    
    public static final class LetYourThisReferenceEscape {
        public final int x;
        
        public LetYourThisReferenceEscape(int y) {
            yourThisReferenceMayNotBeProperlyConstructed(this);
            this.x = y;
        }
    }

    public static final class SneakyLetYourThisReferenceEscape {
        public final int x;
        
        public SneakyLetYourThisReferenceEscape(int y) {
            SneakyLetYourThisReferenceEscape s = this;
            yourThisReferenceMayNotBeProperlyConstructed(s);
            this.x = y;
        }
    }
    
    public static void yourThisReferenceMayNotBeProperlyConstructed(LetYourThisReferenceEscape s) {
        int thisMightNotBeAssigned = s.x;
        System.out.printf("What is value of x now ?", thisMightNotBeAssigned);
    }
    public static void yourThisReferenceMayNotBeProperlyConstructed(SneakyLetYourThisReferenceEscape s) {
        int thisMightNotBeAssigned = s.x;
        System.out.printf("What is value of x now ?", thisMightNotBeAssigned);
    }
}
