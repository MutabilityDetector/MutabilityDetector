package org.mutabilitydetector.benchmarks.mutabletofield.array;

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


import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.AnalysisResultTheory;
import org.mutabilitydetector.benchmarks.types.ClassWithAllPrimitives;
import org.mutabilitydetector.checkers.ArrayFieldMutabilityChecker;
import org.mutabilitydetector.checkers.AsmMutabilityChecker;
import org.mutabilitydetector.locations.CodeLocation.FieldLocation;
import org.mutabilitydetector.locations.CodeLocationFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mutabilitydetector.IsImmutable.IMMUTABLE;
import static org.mutabilitydetector.IsImmutable.NOT_IMMUTABLE;
import static org.mutabilitydetector.TestMatchers.hasReasons;
import static org.mutabilitydetector.TestUtil.runChecker;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areNotImmutable;
import static org.mutabilitydetector.unittesting.matchers.IsImmutableMatcher.hasIsImmutableStatusOf;

@RunWith(Theories.class)
public class ArrayFieldMutabilityCheckerTest {

    private AsmMutabilityChecker checker;
    private AnalysisResult result;

    @Before
    public void setUp() {
        checker = new ArrayFieldMutabilityChecker(CodeLocationFactory.createSimple());
    }

    @Test
    public void arrayTypesAreInherentlyMutable() throws Exception {
        result = runChecker(checker, ClassWithAllPrimitives.Array.class);

        assertThat(checker, hasReasons());
        assertThat(result, areNotImmutable());
    }

    @Test
    public void arrayFieldWhichIsStaticAllowsClassToRemainImmutable() throws Exception {
        result = runChecker(checker, ImmutableWhenArrayFieldIsStatic.class);

        assertThat(result, areImmutable());
    }

    @Test
    public void arrayFieldCodeLocationIsFieldLocationWithNameOfField() throws Exception {
        runChecker(checker, ClassWithAllPrimitives.Array.class);
        FieldLocation sourceLocation = (FieldLocation) checker.checkerResult().reasons.iterator().next().codeLocation();

        assertThat(sourceLocation.typeName(), is(ClassWithAllPrimitives.Array.class.getName()));
        assertThat(sourceLocation.fieldName(), is("anArray"));
    }

    @DataPoints
    public static final AnalysisResultTheory[] classes = new AnalysisResultTheory[]{
            AnalysisResultTheory.of(ImmutableButHasUnmodifiedArrayAsField.class, IMMUTABLE),
            AnalysisResultTheory.of(ImmutableByDefensivelyCopyingAndGuardingArray.class, IMMUTABLE),
            AnalysisResultTheory.of(ImmutableWhenArrayFieldIsStatic.class, IMMUTABLE),
            AnalysisResultTheory.of(MutableAsElementsOfArrayAreMutableAndPublished.class, NOT_IMMUTABLE),
            AnalysisResultTheory.of(MutableByAssigningPrimitiveArrayToField.class, NOT_IMMUTABLE),
            AnalysisResultTheory.of(MutableByHavingArrayTypeAsField.class, NOT_IMMUTABLE),
            AnalysisResultTheory.of(MutableByPublishingArray.class, NOT_IMMUTABLE),};


    @Ignore
    @Theory
    public void correctlyAnalyses(AnalysisResultTheory expected) throws Exception {
        assertThat(runChecker(checker, expected.clazz), hasIsImmutableStatusOf(expected.expected));
    }

}
