package org.mutabilitydetector;

import static org.junit.Assert.assertEquals;
import static org.mutabilitydetector.IsImmutable.EFFECTIVELY_IMMUTABLE;
import static org.mutabilitydetector.IsImmutable.IMMUTABLE;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;
import org.mutabilitydetector.unittesting.MutabilityAssertionError;

public class VanillaJUnitMatcherCompatibility {

    public final static class CheckThisClass {
        public int reassignMe;
    }
    
    @Test
    public void isImmutable() throws Exception {
        assertInstancesOf(CheckThisClass.class, areImmutable(), new AllowAnyReason());
    }

    @Test
    public void hasSomeSortOfDecentErrorMessage() throws Exception {
        try {
            assertInstancesOf(CheckThisClass.class, new IsEitherImmutableOrEffectivelyImmutable(), new AllowNothing());
        } catch (MutabilityAssertionError e) {
            assertEquals(e.getMessage(), 
                    "\n" +
            		"Expected: either Immutable or Effectively Immutable\n" + 
            		"     but: org.mutabilitydetector.VanillaJUnitMatcherCompatibility$CheckThisClass is actually NOT_IMMUTABLE\n" + 
            		"    Reasons:\n" + 
            		"        Field is not final, if shared across threads the Java Memory Model will not guarantee it is initialised before it is read. [Field: reassignMe, Class: org.mutabilitydetector.VanillaJUnitMatcherCompatibility$CheckThisClass]\n" + 
            		"        Field is visible outwith this class, and is not declared final. [Field: reassignMe, Class: org.mutabilitydetector.VanillaJUnitMatcherCompatibility$CheckThisClass]\n" + 
            		"    Allowed reasons:\n" + 
            		"        None.");
        }
    }
    
    public static class AllowAnyReason extends BaseMatcher<MutableReasonDetail> {

        public boolean matches(Object arg0) {
            return true;
        }

        @Override
        public void describeTo(Description arg0) { }
        
    }
    
    public static class AllowNothing extends BaseMatcher<MutableReasonDetail> {

        @Override
        public boolean matches(Object arg0) {
            return false;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("an object that never satisfies");
        }
        
    }
    
    public static class IsEitherImmutableOrEffectivelyImmutable extends BaseMatcher<AnalysisResult> {

        @Override
        public boolean matches(Object item) {
            AnalysisResult result = (AnalysisResult) item;
            return result.isImmutable == IMMUTABLE || result.isImmutable == EFFECTIVELY_IMMUTABLE;
        }

        @Override
        public void describeTo(Description arg0) {
            arg0.appendText("either Immutable or Effectively Immutable");
        }
        
    }
}
