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
package org.mutabilitydetector.benchmarks;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mutabilitydetector.ImmutableAssert.assertImmutable;
import static org.mutabilitydetector.ImmutableAssert.assertNotImmutable;
import static org.mutabilitydetector.TestMatchers.hasNoReasons;
import static org.mutabilitydetector.TestUtil.runChecker;

import org.junit.Before;
import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.checkers.IMutabilityChecker;
import org.mutabilitydetector.checkers.PublishedNonFinalFieldChecker;
import org.mutabilitydetector.locations.FieldLocation;

public class PublishedNonFinalFieldCheckerTest {

    private IMutabilityChecker checker;
    private AnalysisResult result;

    @Before
    public void setUp() {
        checker = new PublishedNonFinalFieldChecker();
    }

    @Test
    public void immutableExamplePassesCheck() throws Exception {
        result = runChecker(checker, ImmutableExample.class);

        assertThat(checker, hasNoReasons());
        assertImmutable(result);
    }

    @Test
    public void classWithPublicNonFinalFieldFailsCheck() throws Exception {
        result = runChecker(checker, MutableByHavingPublicNonFinalField.class);
        assertNotImmutable(result);
    }

    @Test
    public void classWithProtectedNonFinalFieldFailsCheck() throws Exception {
        result = runChecker(checker, MutableByHavingProtectedNonFinalField.class);
        assertNotImmutable(result);
    }

    @Test
    public void classWithDefaultVisibleNonFinalFieldFailsCheck() throws Exception {
        result = runChecker(checker, MutableByHavingDefaultVisibleNonFinalField.class);
        assertNotImmutable(result);
    }

    @Test
    public void classWithPublicFinalFieldPassesCheck() throws Exception {
        result = runChecker(checker, ImmutableWithPublicFinalField.class);
        assertThat(checker, hasNoReasons());
        assertImmutable(result);
    }

    @Test
    public void addsFieldLocation() throws Exception {
        result = runChecker(checker, MutableByHavingDefaultVisibleNonFinalField.class);
        FieldLocation fieldLocation = (FieldLocation) result.reasons.iterator().next().codeLocation();
        assertThat(fieldLocation.typeName(), is(MutableByHavingDefaultVisibleNonFinalField.class.getName()));
        assertThat(fieldLocation.fieldName(), is("name"));
    }
}
