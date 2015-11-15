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



import static org.mutabilitydetector.unittesting.AllowedReason.provided;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areNotImmutable;

import org.junit.Ignore;
import org.junit.Test;
import org.mutabilitydetector.benchmarks.settermethod.singlecheck.WithAlias;

public final class ValidSingleCheckLazyInitialisationWithAlias {

    @Ignore
    @Test
    public void byteWithJvmInitialValue() {
        final Class<?> klasse = WithAlias.WithJvmInitialValue.ByteValid.class;
        AsserterWithLazyInitialisationAlgorithm.ASSERTER.assertImmutable(klasse);
    }

    @Ignore
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