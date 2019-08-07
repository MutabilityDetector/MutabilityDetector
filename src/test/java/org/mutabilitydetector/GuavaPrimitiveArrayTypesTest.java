package org.mutabilitydetector;

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

import com.google.common.primitives.ImmutableDoubleArray;
import com.google.common.primitives.ImmutableIntArray;
import com.google.common.primitives.ImmutableLongArray;
import org.junit.Rule;
import org.junit.Test;
import org.mutabilitydetector.junit.FalsePositive;
import org.mutabilitydetector.junit.IncorrectAnalysisRule;

import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;

public class GuavaPrimitiveArrayTypesTest {

    @Rule
    public IncorrectAnalysisRule incorrectAnalysisRule = new IncorrectAnalysisRule();

    @FalsePositive("Field can have a mutable type (an array) assigned to it; Field is an array")
    @Test
    public void immutableIntArray() {
        assertInstancesOf(ImmutableIntArray.class, areImmutable());
    }

    @FalsePositive("Field can have a mutable type (an array) assigned to it; Field is an array")
    @Test
    public void immutableDoubleArray() {
        assertInstancesOf(ImmutableDoubleArray.class, areImmutable());
    }

    @FalsePositive("Field can have a mutable type (an array) assigned to it; Field is an array")
    @Test
    public void immutableLongArray() {
        assertInstancesOf(ImmutableLongArray.class, areImmutable());
    }
}
