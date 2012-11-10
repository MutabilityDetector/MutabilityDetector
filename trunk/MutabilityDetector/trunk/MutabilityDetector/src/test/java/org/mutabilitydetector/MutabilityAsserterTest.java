package org.mutabilitydetector;

import java.util.Date;

import org.junit.Test;
import org.mutabilitydetector.unittesting.MutabilityAsserter;
import org.mutabilitydetector.unittesting.MutabilityMatchers;

public class MutabilityAsserterTest {

    @SuppressWarnings("unused")
    private static final class HasADateField {
        private final Date date;
        public HasADateField(Date date) {
            this.date = date;
        }
        
    }
    
    @Test
    public void canBuildConfigurationForAnalysisSessionToIncludeHardcodedResults() throws Exception {
        MutabilityAsserter asserter = MutabilityAsserter.configured(new ConfigurationBuilder() {
            @Override
            public void configure() {
                hardcodeAsDefinitelyImmutable(Date.class);
            }
        });
        
        asserter.assertImmutable(HasADateField.class);
    }

    @Test
    public void assertingOnAHardcodedResultDirectlyPerformsTheRealAnalysis() throws Exception {
        MutabilityAsserter asserter = MutabilityAsserter.configured(new ConfigurationBuilder() {
            @Override
            public void configure() {
                hardcodeAsDefinitelyImmutable(HasADateField.class);
            }
        });
        
        asserter.assertInstancesOf(HasADateField.class, MutabilityMatchers.areNotImmutable());
    }

}
