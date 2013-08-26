package org.mutabilitydetector.unittesting.linkedsourcelocationspike;

import org.junit.Ignore;
import org.junit.Test;

public class UseStackTraceElementInAssertionResult {
    
    @Test @Ignore
    public void assertStringIsImmutable() throws MutabilityAssertionError {
        StackTraceElement pointingAtHashFieldInStringSourceCode = new StackTraceElement("java.lang.String", "<init>", "String.java", 105);
        StackTraceElement pointingAtSomeOtherType = new StackTraceElement("org.mutabilitydetector.benchmarks.ImmutableExample", "<init>", "ImmutableExample.java", 24);
        
        MutabilityAssertionCause someOtherReason = new MutabilityAssertionCause("Some other reason", pointingAtSomeOtherType, null);
        MutabilityAssertionCause reassignableHashField = new MutabilityAssertionCause("Field can be reassigned", pointingAtHashFieldInStringSourceCode, someOtherReason);
        throw new MutabilityAssertionError("\nExpected java.lang.String to be Immutable\n but was not Immutable. \n\tReasons:", 
                                           reassignableHashField);

    }

    public static void main(String[] args) {
        new UseStackTraceElementInAssertionResult().assertStringIsImmutable();
    }
}