package org.mutabilitydetector.benchmarks.settermethod;

/*
 * #%L
 * MutabilityDetector
 * %%
 * Copyright (C) 2008 - 2014 Graham Allan
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */



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