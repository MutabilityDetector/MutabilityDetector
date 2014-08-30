package org.mutabilitydetector.benchmarks;

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



import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mutabilitydetector.IsImmutable.IMMUTABLE;
import static org.mutabilitydetector.IsImmutable.NOT_IMMUTABLE;
import static org.mutabilitydetector.TestMatchers.hasReasons;
import static org.mutabilitydetector.TestUtil.runChecker;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutable;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areNotImmutable;
import static org.mutabilitydetector.unittesting.matchers.IsImmutableMatcher.hasIsImmutableStatusOf;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.AnalysisResultTheory;
import org.mutabilitydetector.benchmarks.sealed.HasFinalFieldsAndADefaultConstructor;
import org.mutabilitydetector.benchmarks.sealed.ImmutableByHavingOnlyPrivateConstructors;
import org.mutabilitydetector.benchmarks.sealed.IsFinalAndHasOnlyPrivateConstructors;
import org.mutabilitydetector.benchmarks.sealed.MutableByNotBeingFinalClass;
import org.mutabilitydetector.benchmarks.sealed.SealedImmutable;
import org.mutabilitydetector.benchmarks.types.EnumType;
import org.mutabilitydetector.checkers.CanSubclassChecker;
import org.mutabilitydetector.locations.ClassLocation;
import org.mutabilitydetector.unittesting.MutabilityAssert;

@RunWith(Theories.class)
public class CanSubclassCheckerTest {

    private CanSubclassChecker checker;

    @Before
    public void createChecker() {
        checker = new CanSubclassChecker();
    }
    
    @DataPoints public static final AnalysisResultTheory[] classes = new AnalysisResultTheory[] {
        AnalysisResultTheory.of(MutableByNotBeingFinalClass.class, NOT_IMMUTABLE),
        AnalysisResultTheory.of(SealedImmutable.class, NOT_IMMUTABLE),
        AnalysisResultTheory.of(HasFinalFieldsAndADefaultConstructor.class, NOT_IMMUTABLE),
        AnalysisResultTheory.of(IsFinalAndHasOnlyPrivateConstructors.class, IMMUTABLE),
        AnalysisResultTheory.of(ImmutableByHavingOnlyPrivateConstructors.class, IMMUTABLE),
        AnalysisResultTheory.of(ImmutableByHavingOnlyAPrivateConstructorUsingTheBuilderPattern.class, IMMUTABLE),
    };

    @Theory
    public void correctlyAnalyses(AnalysisResultTheory expected) throws Exception {
        assertThat(runChecker(checker, expected.clazz), hasIsImmutableStatusOf(expected.expected));
    }
    
    @Test
    public void aClassWhichIsNotFinalIsNotImmutable() throws Exception {
        AnalysisResult result = runChecker(checker, MutableByNotBeingFinalClass.class);
        assertThat(checker, hasReasons());
        assertThat(result, areNotImmutable());
    }

    @Test
    public void immutableExampleIsReportedAsImmutable() throws Exception {
        assertImmutable(ImmutableExample.class);
    }

    @Test
    public void enumTypeIsImmutable() throws Exception {
        assertImmutable(EnumType.class);
    }

    @Test
    public void hasCodeLocationWithCorrectTypeName() throws Exception {
        runChecker(checker, MutableByNotBeingFinalClass.class);
        ClassLocation location = (ClassLocation) checker.reasons().iterator().next().codeLocation();
        assertThat(location.typeName(), is(MutableByNotBeingFinalClass.class.getName()));
    }
    
    @Test
    public void privateConstructorsUsingBuilderPatternAreImmutable() throws Exception {
        MutabilityAssert.assertImmutable(ImmutableByHavingOnlyAPrivateConstructorUsingTheBuilderPattern.class);
    }
}
