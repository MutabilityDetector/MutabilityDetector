package org.mutabilitydetector;

import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;
import net.ttsui.junit.rules.pending.PendingImplementation;
import net.ttsui.junit.rules.pending.PendingRule;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.mutabilitydetector.unittesting.MutabilityAssert;

public class VanillaJUnitMatcherCompatibility {

    @Rule public MethodRule pendingRule = new PendingRule();
    
    public final static class CheckThisClass {
        public int reassignMe;
    }
    
    @PendingImplementation("Fails when attempting to invoke describeMismatch, which didn't exist in hamcrest 1.1")
    @Test
    public void isImmutable() throws Exception {
        MutabilityAssert.assertInstancesOf(CheckThisClass.class, areImmutable(), new AllowAnyReason());
    }
    
    public static class AllowAnyReason extends BaseMatcher<MutableReasonDetail> {

        public static Matcher<MutableReasonDetail> allowingAReasonThatMatches() {
            return new AllowAnyReason();
        }
        
        @Override
        public boolean matches(Object arg0) {
            return true;
        }

        @Override
        public void describeTo(Description arg0) { }
        
    }
}
