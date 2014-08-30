package org.mutabilitydetector.benchmarks.inheritance;

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



import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutable;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areNotImmutable;

import org.junit.Test;
import org.mutabilitydetector.benchmarks.MutableByExtendingMutableType;
import org.mutabilitydetector.benchmarks.types.AbstractType.ImmutableSubtypeOfAbstractType;

public class TestSubclassingOfMutableType {

    @Test
    public void testSupertypeIsEffectivelyImmutable() throws Exception {
        assertInstancesOf(ImmutableSupertype.class, areNotImmutable());
    }

    @Test
    public void testImmutableSubtypeIsReportedAsImmutable() throws Exception {
        assertImmutable(ImmutableSubtypeOfImmutableSupertype.class);
    }

    @Test
    public void mutableSubtype() throws Exception {
        assertInstancesOf(MutableSubtypeOfImmutableSupertype.class, areNotImmutable());
    }

    @Test
    public void classExtendingObjectIsNotRenderedMutable() throws Exception {
        assertImmutable(ImmutableSubtypeWithNoSuperclass.class);
    }

    @Test
    public void classIsNotMadeMutableJustByExtendingAbstractClass() throws Exception {
        assertImmutable(ImmutableSubtypeOfAbstractType.class);
    }

    @Test
    public void immutableSubclassIsMutableIfSuperclassIsMutable() throws Exception {
        assertInstancesOf(MutableByExtendingMutableType.class, areNotImmutable());
    }
}
