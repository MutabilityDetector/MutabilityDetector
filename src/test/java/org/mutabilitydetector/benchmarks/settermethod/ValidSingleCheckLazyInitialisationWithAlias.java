package org.mutabilitydetector.benchmarks.settermethod;

import static org.mutabilitydetector.unittesting.AllowedReason.provided;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areNotImmutable;

import org.junit.Test;
import org.mutabilitydetector.benchmarks.settermethod.singlecheck.WithAlias;

public final class ValidSingleCheckLazyInitialisationWithAlias {

    @Test
    public void byteWithJvmInitialValue() {
        final Class<?> klasse = WithAlias.WithJvmInitialValue.ByteValid.class;
        AsserterWithLazyInitialisationAlgorithm.ASSERTER.assertImmutable(klasse);
    }

    @Test
    public void shortWithJvmInitialValue() {
        final Class<?> klasse = WithAlias.WithJvmInitialValue.ShortValid.class;
        AsserterWithLazyInitialisationAlgorithm.ASSERTER.assertImmutable(klasse);
    }

    @Test
    public void floatWithJvmInitialValue() {
        final Class<?> klasse = WithAlias.WithJvmInitialValue.FloatValid.class;
        AsserterWithLazyInitialisationAlgorithm.ASSERTER.assertImmutable(klasse);
    }

    @Test
    public void javaLangString() {
        final Class<?> klasse = String.class;
        AsserterWithLazyInitialisationAlgorithm.ASSERTER.assertInstancesOf(klasse, areNotImmutable());
    }

    @Test
    public void stringWithJvmInitialValue() {
        final Class<?> klasse = WithAlias.WithJvmInitialValue.StringValid.class;
        AsserterWithLazyInitialisationAlgorithm.ASSERTER.assertInstancesOf(klasse, areImmutable(), provided(String.class).isAlsoImmutable());
    }

    @Test
    public void stringWithCustomInitialValue() {
        final Class<?> klasse = WithAlias.WithCustomInitialValue.StringValid.class;
        AsserterWithLazyInitialisationAlgorithm.ASSERTER.assertInstancesOf(klasse, areImmutable(), provided(String.class).isAlsoImmutable());
    }

    @Test
    public void integerWithCustomInitialValue() {
        final Class<?> klasse = WithAlias.WithCustomInitialValue.IntegerValid.class;
        AsserterWithLazyInitialisationAlgorithm.ASSERTER.assertInstancesOf(klasse, areImmutable(), provided(String.class).isAlsoImmutable());
    }

}