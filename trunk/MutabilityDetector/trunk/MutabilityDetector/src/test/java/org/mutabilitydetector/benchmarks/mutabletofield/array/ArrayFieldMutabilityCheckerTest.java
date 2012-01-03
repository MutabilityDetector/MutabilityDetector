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
package org.mutabilitydetector.benchmarks.mutabletofield.array;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mutabilitydetector.ImmutableAssert.assertImmutable;
import static org.mutabilitydetector.ImmutableAssert.assertNotImmutable;
import static org.mutabilitydetector.TestMatchers.hasReasons;
import static org.mutabilitydetector.TestUtil.runChecker;

import org.junit.Before;
import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.benchmarks.types.ClassWithAllPrimitives;
import org.mutabilitydetector.checkers.ArrayFieldMutabilityChecker;
import org.mutabilitydetector.checkers.IMutabilityChecker;
import org.mutabilitydetector.locations.FieldLocation;

public class ArrayFieldMutabilityCheckerTest {

    private IMutabilityChecker checker;
    private AnalysisResult result;

    @Before
    public void setUp() {
        checker = new ArrayFieldMutabilityChecker();
    }

    @Test
    public void arrayTypesAreInherentlyMutable() throws Exception {
        result = runChecker(checker, ClassWithAllPrimitives.Array.class);

        assertThat(checker, hasReasons());
        assertNotImmutable(result);
    }

    @Test
    public void arrayFieldWhichIsStaticAllowsClassToRemainImmutable() throws Exception {
        result = runChecker(checker, ImmutableWhenArrayFieldIsStatic.class);

        assertImmutable(result);
    }

    @Test
    public void arrayFieldCodeLocationIsFieldLocationWithNameOfField() throws Exception {
        runChecker(checker, ClassWithAllPrimitives.Array.class);
        FieldLocation sourceLocation = (FieldLocation) checker.reasons().iterator().next().codeLocation();

        assertThat(sourceLocation.typeName(), is(ClassWithAllPrimitives.Array.class.getName()));
        assertThat(sourceLocation.fieldName(), is("anArray"));
    }

}
