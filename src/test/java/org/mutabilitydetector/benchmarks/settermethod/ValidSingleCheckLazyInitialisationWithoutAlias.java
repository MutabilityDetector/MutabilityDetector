package org.mutabilitydetector.benchmarks.settermethod;

import static org.mutabilitydetector.unittesting.AllowedReason.provided;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areNotImmutable;

import org.junit.Test;
import org.mutabilitydetector.benchmarks.settermethod.singlecheck.WithoutAlias;

public final class ValidSingleCheckLazyInitialisationWithoutAlias {
    
    @Test
    public void booleanFlagRendersImmutable() {
        final Class<?> klasse = WithoutAlias.WithJvmInitialValue.BooleanFlag.class;
        AsserterWithLazyInitialisationAlgorithm.ASSERTER.assertImmutable(klasse);
    }

    @Test
    public void booleanFlagWithFalseAssignmentGuardRendersNotImmutable() {
        final Class<?> klasse = WithoutAlias.WithJvmInitialValue.BooleanFlagWithFalseAssignmentGuard.class;
        AsserterWithLazyInitialisationAlgorithm.ASSERTER.assertInstancesOf(klasse, areNotImmutable());
    }

    @Test
    public void verifyIntegerWithJvmInitial() {
        final Class<?> klasse = WithoutAlias.WithJvmInitialValue.IntegerValid.class;
        AsserterWithLazyInitialisationAlgorithm.ASSERTER.assertImmutable(klasse);
    }

    @Test
    public void integerWithJvmInitial() {
        final Class<?> klasse = WithoutAlias.WithJvmInitialValue.IntegerValid.class;
        AsserterWithLazyInitialisationAlgorithm.ASSERTER.assertImmutable(klasse);
    }

    @Test
    public void floatWithJvmInitialValue() {
        final Class<?> klasse = WithoutAlias.WithJvmInitialValue.FloatValid.class;
        AsserterWithLazyInitialisationAlgorithm.ASSERTER.assertImmutable(klasse);
    }

    @Test
    public void charWithJvmInitial() {
        final Class<?> klasse = WithoutAlias.WithJvmInitialValue.CharValid.class;
        AsserterWithLazyInitialisationAlgorithm.ASSERTER.assertImmutable(klasse);
    }

    @Test
    public void objectWithJvmInitialValue() {
        final Class<?> klasse = WithoutAlias.WithJvmInitialValue.ObjectValid.class;
        AsserterWithLazyInitialisationAlgorithm.ASSERTER.assertInstancesOf(klasse, areImmutable(), provided(Object.class).isAlsoImmutable());
    }

    @Test
    public void integerWithCustomInitialValue() {
        final Class<?> klasse = WithoutAlias.WithCustomInitialValue.IntegerValid.class;
        AsserterWithLazyInitialisationAlgorithm.ASSERTER.assertImmutable(klasse);
    }

    @Test
    public void floatWithCustomInitialValue() {
        final Class<?> klasse = WithoutAlias.WithCustomInitialValue.FloatValid.class;
        AsserterWithLazyInitialisationAlgorithm.ASSERTER.assertImmutable(klasse);
    }

    @Test
    public void customObjectWithJvmInitialValue() {
        final Class<?> klasse = WithoutAlias.WithJvmInitialValue.CustomObjectInvalid.class;
        AsserterWithLazyInitialisationAlgorithm.ASSERTER.assertInstancesOf(klasse, areNotImmutable());
    }

}
