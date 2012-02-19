/*
 *    Copyright (c) 2008-2011 Graham Allan
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package org.mutabilitydetector.benchmarks.inheritance;

import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutable;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areNotImmutable;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.mutabilitydetector.benchmarks.types.EnumType;
import org.mutabilitydetector.junit.FalseNegative;
import org.mutabilitydetector.junit.IncorrectAnalysisRule;

public class TestSubclassingOfImmutableType {

    @Rule public MethodRule rule = new IncorrectAnalysisRule();
    
    @Test
    public void supertypeIsDefinitelyNotImmutable() throws Exception {
        assertInstancesOf(MutableSupertype.class, areNotImmutable());
    }

    @FalseNegative("InheritedMutabilityChecker doesn't work properly yet.")
    @Test
    public void immutableSubtypeIsReportedAsMutable() throws Exception {
        assertImmutable(ImmutableSubtypeOfMutableSupertype.class);
    }

    @Test
    public void mutableSubtype() throws Exception {
        assertInstancesOf(MutableSubtypeOfMutableSupertype.class, areNotImmutable());
    }

    @Test
    public void enumTypeIsDefinitelyImmutable() throws Exception {
        assertImmutable(EnumType.class);
    }
}
