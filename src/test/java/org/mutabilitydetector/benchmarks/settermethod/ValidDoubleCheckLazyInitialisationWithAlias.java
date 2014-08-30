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