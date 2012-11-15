package org.mutabilitydetector.demo.workinprogress;

import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutable;

import org.junit.Ignore;
import org.junit.Test;

public class IsThatAnArray___PANIC {

    @Test @Ignore
    public void classWithASafelyUsedArrayField() throws Exception {
        assertImmutable(SafelyUseAnArray.class);
    }
    
    public final static class SafelyUseAnArray {
        private final int[] arrayField;
        
        public SafelyUseAnArray(int first, int second) {
            arrayField = new int[] { first, second };
        }
        
        public int first() {
            return arrayField[0];
        }
        
        public int second() {
            return arrayField[1];
        }
    }
}
