package org.mutabilitydetector;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mutabilitydetector.MutabilityReason.ABSTRACT_TYPE_INHERENTLY_MUTABLE;
import static org.mutabilitydetector.MutabilityReason.ESCAPED_THIS_REFERENCE;
import static org.mutabilitydetector.MutabilityReason.MUTABLE_TYPE_TO_FIELD;
import static org.mutabilitydetector.MutabilityReason.NON_FINAL_FIELD;

import org.junit.Test;

public class MutabilityReasonTest {

    @Test public void isOneOfReturnsTrue() {
         assertTrue(NON_FINAL_FIELD.isOneOf(NON_FINAL_FIELD));
    }
    
    @Test public void isOneOfReturnsTrueWithSeveralArguments() {
        assertTrue(NON_FINAL_FIELD.isOneOf(ABSTRACT_TYPE_INHERENTLY_MUTABLE, NON_FINAL_FIELD, MUTABLE_TYPE_TO_FIELD));
   }
    
    @Test public void isOneOfReturnsFalseWhenNotEqualToAnyOfGivenReasons() {
        assertFalse(ESCAPED_THIS_REFERENCE.isOneOf(ABSTRACT_TYPE_INHERENTLY_MUTABLE, NON_FINAL_FIELD, MUTABLE_TYPE_TO_FIELD));
    }
}
