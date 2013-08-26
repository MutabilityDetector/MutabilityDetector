package org.mutabilitydetector.benchmarks.settermethod;

import static org.mutabilitydetector.unittesting.MutabilityMatchers.areEffectivelyImmutable;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areNotImmutable;

import org.junit.Test;
import org.mutabilitydetector.benchmarks.settermethod.singlecheck.WithoutAlias;

public final class InvalidSingleCheckLazyInitialisationWithoutAlias {

    @Test
    public void charWithJvmInitialValue() {
        final Class<?> klasse = WithoutAlias.WithJvmInitialValue.CharInvalid.class;
        AsserterWithLazyInitialisationAlgorithm.ASSERTER.assertInstancesOf(klasse, areNotImmutable());
    }

    @Test
    public void integerWithJvmInitialValue() {
        final Class<?> klasse = WithoutAlias.WithJvmInitialValue.IntegerInvalid.class;
        AsserterWithLazyInitialisationAlgorithm.ASSERTER.assertInstancesOf(klasse, areNotImmutable());
    }

    @Test
    public void floatWithJvmInitialValue() {
        final Class<?> klasse = WithoutAlias.WithJvmInitialValue.FloatInvalid.class;
        AsserterWithLazyInitialisationAlgorithm.ASSERTER.assertInstancesOf(klasse, areNotImmutable());
    }

    @Test
    public void objectWithJvmInitialValue() {
        final Class<?> klasse = WithoutAlias.WithJvmInitialValue.ObjectInvalid.class;
        AsserterWithLazyInitialisationAlgorithm.ASSERTER.assertInstancesOf(klasse, areNotImmutable());
    }

    @Test
    public void stringWithJvmInitialValue() {
        final Class<?> klasse = WithoutAlias.WithJvmInitialValue.StringInvalid.class;
        AsserterWithLazyInitialisationAlgorithm.ASSERTER.assertInstancesOf(klasse, areNotImmutable());
    }

    @Test
    public void floatWithMultipleCustomInitialValues() {
        final Class<?> klasse = WithoutAlias.WithCustomInitialValue.FloatInvalidWithMultipleInitialValues.class;
        AsserterWithLazyInitialisationAlgorithm.ASSERTER.assertInstancesOf(klasse, areNotImmutable());
    }

    @Test
    public void integerWithCustomInitialValue() {
        final Class<?> klasse = WithoutAlias.WithCustomInitialValue.IntegerInvalid.class;
        AsserterWithLazyInitialisationAlgorithm.ASSERTER.assertInstancesOf(klasse, areNotImmutable());
    }

    @Test
    public void stringWithCustomInitialValue() {
        final Class<?> klasse = WithoutAlias.WithCustomInitialValue.StringInvalid.class;
        AsserterWithLazyInitialisationAlgorithm.ASSERTER.assertInstancesOf(klasse, areNotImmutable());
    }

    @Test
    public void integerWithNonCandidateVariableRendersEffectivelyImmutable() {
        final Class<?> klasse = WithoutAlias.WithCustomInitialValue.IntegerWithNonCandidateVariable.class;
        AsserterWithLazyInitialisationAlgorithm.ASSERTER.assertInstancesOf(klasse, areEffectivelyImmutable());
    }

    @Test
    public void integerWithInvalidValueCalculationMethodRendersMutable() {
        final Class<?> klasse = WithoutAlias.WithJvmInitialValue.IntegerWithInvalidValueCalculationMethod.class;
        AsserterWithLazyInitialisationAlgorithm.ASSERTER.assertInstancesOf(klasse, areNotImmutable());
    }

    @Test
    public void stringWithInvalidValueCalculationMethodRendersMutable() {
        final Class<?> klasse = WithoutAlias.WithJvmInitialValue.StringWithInvalidValueCalculationMethod.class;
        AsserterWithLazyInitialisationAlgorithm.ASSERTER.assertInstancesOf(klasse, areNotImmutable());
    }

}