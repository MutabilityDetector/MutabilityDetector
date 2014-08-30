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
import static org.mutabilitydetector.TestMatchers.hasNoReasons;
import static org.mutabilitydetector.TestUtil.runChecker;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areNotImmutable;

import org.junit.Before;
import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.checkers.AsmMutabilityChecker;
import org.mutabilitydetector.checkers.PublishedNonFinalFieldChecker;
import org.mutabilitydetector.locations.FieldLocation;

public class PublishedNonFinalFieldCheckerTest {

    private AsmMutabilityChecker checker;
    private AnalysisResult result;

    @Before
    public void setUp() {
        checker = new PublishedNonFinalFieldChecker();
    }

    @Test
    public void immutableExamplePassesCheck() throws Exception {
        result = runChecker(checker, ImmutableExample.class);

        assertThat(checker, hasNoReasons());
        assertThat(result, areImmutable());
        
    }

    @Test
    public void classWithPublicNonFinalFieldFailsCheck() throws Exception {
        result = runChecker(checker, MutableByHavingPublicNonFinalField.class);
        assertThat(result, areNotImmutable());
    }

    @Test
    public void classWithProtectedNonFinalFieldFailsCheck() throws Exception {
        result = runChecker(checker, MutableByHavingProtectedNonFinalField.class);
        assertThat(result, areNotImmutable());
    }

    @Test
    public void classWithDefaultVisibleNonFinalFieldFailsCheck() throws Exception {
        result = runChecker(checker, MutableByHavingDefaultVisibleNonFinalField.class);
        assertThat(result, areNotImmutable());
    }

    @Test
    public void classWithPublicFinalFieldPassesCheck() throws Exception {
        result = runChecker(checker, ImmutableWithPublicFinalField.class);
        assertThat(checker, hasNoReasons());
        assertThat(result, areImmutable());
    }

    @Test
    public void addsFieldLocation() throws Exception {
        result = runChecker(checker, MutableByHavingDefaultVisibleNonFinalField.class);
        FieldLocation fieldLocation = (FieldLocation) result.reasons.iterator().next().codeLocation();
        assertThat(fieldLocation.typeName(), is(MutableByHavingDefaultVisibleNonFinalField.class.getName()));
        assertThat(fieldLocation.fieldName(), is("name"));
    }
}
