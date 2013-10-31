package org.mutabilitydetector;

import org.junit.Test;
import org.mutabilitydetector.unittesting.MutabilityAsserter;
import org.mutabilitydetector.unittesting.MutabilityAssertionError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mutabilitydetector.unittesting.AllowedReason.allowingNonFinalFields;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutable;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;

/**
 * @author: Marc Gomez / marc.gomez82 (at) gmail.com
 */
public class ConsolidatedReasonsTest {

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PublishedNonFinalFieldChecker: code location points to the field and class (correct).
    // Potential improvements: Line number. Field declaration with access modifiers, etc... Avoid multiple reasons (PUBLISHED_NON_FINAL_FIELD + NON_FINAL_FIELD)
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public final static class ClassWithPublicNonFinalField {
        public String publicField;
    }


    @Test
    public void isImmutablePublishedNonFinalField() throws Exception {
        try {
            assertInstancesOf(ClassWithPublicNonFinalField.class, areImmutable());
            fail("Error should be thrown");
        } catch (MutabilityAssertionError e) {
            assertThat(e.getMessage(), is("\n"+
                    "Expected: org.mutabilitydetector.ConsolidatedReasonsTest$ClassWithPublicNonFinalField to be IMMUTABLE\n" +
                    "     but: org.mutabilitydetector.ConsolidatedReasonsTest$ClassWithPublicNonFinalField is actually NOT_IMMUTABLE\n" +
                    "    Reasons:\n" +
                    "        Field is visible outwith this class, and is not declared final. [Field: publicField, Class: org.mutabilitydetector.ConsolidatedReasonsTest$ClassWithPublicNonFinalField]\n" +
                    //"        Field is not final, if shared across threads the Java Memory Model will not guarantee it is initialised before it is read. [Field: publicField, Class: org.mutabilitydetector.ConsolidatedReasonsTest$ClassWithPublicNonFinalField]\n" +
                    "    Allowed reasons:\n" +
                    "        None."));
        }
    }
}
