package org.mutabilitydetector.benchmarks.settermethod;

import static org.mutabilitydetector.unittesting.AllowedReason.provided;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areNotImmutable;

import org.junit.Test;
import org.mutabilitydetector.benchmarks.settermethod.doublecheck.WithAlias.WithCustomInitialValue.MessageHolder;
import org.mutabilitydetector.benchmarks.settermethod.doublecheck.WithAlias.WithCustomInitialValue.MessageHolderWithWrongAssignmentGuard;

public final class ValidDoubleCheckLazyInitialisationWithAlias {

    @Test
    public void aliasedValidIntegerWithJvmInitialValue() {
        final Class<?> klasse = org.mutabilitydetector.benchmarks.settermethod.doublecheck.AliasedIntegerWithDefault.class;
        AsserterWithLazyInitialisationAlgorithm.ASSERTER.assertImmutable(klasse);
    }

    @Test
    public void messageHolderRendersImmutable() {
        final Class<?> klasse = MessageHolder.class;
        AsserterWithLazyInitialisationAlgorithm.ASSERTER.assertInstancesOf(klasse, areImmutable(), provided(String.class).isAlsoImmutable());
    }

    @Test
    public void messageHolderWithWrongAssignmentGuardRendersMutable() {
        final Class<?> klasse = MessageHolderWithWrongAssignmentGuard.class;
        AsserterWithLazyInitialisationAlgorithm.ASSERTER.assertInstancesOf(klasse, areNotImmutable(), provided(String.class).isAlsoImmutable());
    }

}