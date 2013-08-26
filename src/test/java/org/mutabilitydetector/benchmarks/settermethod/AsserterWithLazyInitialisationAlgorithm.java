package org.mutabilitydetector.benchmarks.settermethod;

import org.mutabilitydetector.ConfigurationBuilder;
import org.mutabilitydetector.unittesting.MutabilityAsserter;

public class AsserterWithLazyInitialisationAlgorithm {

    static final MutabilityAsserter ASSERTER = MutabilityAsserter.configured(new ConfigurationBuilder() {
        @Override public void configure() {
            useAdvancedReassignedFieldAlgorithm();
        }
    });
}
