/* 
 * Mutability Detector
 *
 * Copyright 2009 Graham Allan
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.mutabilitydetector.benchmarks;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mutabilitydetector.ImmutableAssert.assertEffectivelyImmutable;
import static org.mutabilitydetector.ImmutableAssert.assertImmutable;
import static org.mutabilitydetector.TestMatchers.hasReasons;
import static org.mutabilitydetector.TestUtil.runChecker;

import org.junit.Before;
import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.benchmarks.types.EnumType;
import org.mutabilitydetector.checkers.FinalClassChecker;
import org.mutabilitydetector.locations.ClassLocation;

public class FinalClassCheckerTest {

    private FinalClassChecker checker;

    @Before
    public void createChecker() {
        checker = new FinalClassChecker();
    }

    @Test
    public void aClassWhichIsNotFinalIsMaybeImmutable() throws Exception {
        AnalysisResult result = runChecker(checker, MutableByNotBeingFinalClass.class);
        assertThat(checker, hasReasons());
        assertEffectivelyImmutable(result);
    }

    @Test
    public void immutableExampleIsReportedAsImmutable() throws Exception {
        assertImmutable(runChecker(checker, ImmutableExample.class));
    }

    @Test
    public void enumTypeIsImmutable() throws Exception {
        assertImmutable(runChecker(checker, EnumType.class));
    }

    @Test
    public void hasCodeLocationWithCorrectTypeName() throws Exception {
        runChecker(checker, MutableByNotBeingFinalClass.class);
        ClassLocation location = (ClassLocation) checker.reasons().iterator().next().codeLocation();
        assertThat(location.typeName(), is(MutableByNotBeingFinalClass.class.getName()));
    }

}
