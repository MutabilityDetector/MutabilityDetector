package org.mutabilitydetector;

import static org.mutabilitydetector.unittesting.MutabilityAsserter.configured;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areNotImmutable;

import org.junit.Test;
import org.mutabilitydetector.unittesting.MutabilityAsserter;

public class ConfigureClassesToBeImmutable {
    
    public static final MutabilityAsserter MUTABILITY = configured(new ConfigurationBuilder() {
        @Override public void configure() {
            overrideAsDefinitelyImmutable(DigitsOfPi.class);
        }
    });
    
    @Test
    public void canConfigureClassesToBeOverrideMutabilityDetectorsResult() throws Exception {
        MUTABILITY.assertImmutable(DigitsOfPiFormatter.class);
    }
    
    @Test
    public void hardcodedResultsAreIgnoredWhenTestingClassDirectly() throws Exception {
        MUTABILITY.assertInstancesOf(DigitsOfPi.class, areNotImmutable());
    }

    
    /**
     * MutabilityDetector is wrong, {@link DigitsOfPi} is immutable.
     */
    static final class DigitsOfPi {
        private final int[] sillyWayToStorePi = new int[] { 3, 141 };
        
        public int piDigitsLeftOfDecimalPlace() {
            return sillyWayToStorePi[0];
        }

        public int piDigitsRightOfDecimalPlace() {
            return sillyWayToStorePi[1];
        }
    }
    
    /**
     * {@link DigitsOfPiFormatter} is considered mutable because of the
     * transitivity of mutability, and the incorrect analysis performed
     * on {@link DigitsOfPi}.
     */
    static final class DigitsOfPiFormatter {
        private final DigitsOfPi other;

        public DigitsOfPiFormatter(DigitsOfPi other) {
            this.other = other;
        }
        
        public String formatPi() {
            return other.piDigitsLeftOfDecimalPlace() + "." + other.piDigitsRightOfDecimalPlace();
        }
        
    }
}
