package org.mutabilitydetector.unittesting.linkedsourcelocationspike;

/*
 * #%L
 * MutabilityDetector
 * %%
 * Copyright (C) 2008 - 2014 Graham Allan
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */



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