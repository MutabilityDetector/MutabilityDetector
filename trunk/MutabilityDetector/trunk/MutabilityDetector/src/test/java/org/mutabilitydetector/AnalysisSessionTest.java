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
package org.mutabilitydetector;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.mutabilitydetector.ImmutableAssert.assertImmutable;
import static org.mutabilitydetector.IsImmutable.NOT_IMMUTABLE;
import static org.mutabilitydetector.locations.Dotted.fromClass;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mutabilitydetector.benchmarks.ImmutableExample;
import org.mutabilitydetector.benchmarks.circular.ImmutableClassA;

public class AnalysisSessionTest {

    private Class<ImmutableExample> immutableClass;

    @Before
    public void setUp() {
        immutableClass = ImmutableExample.class;
    }

    @Test
    public void analysisOfImmutableExampleWillBeRegistered() throws Exception {
        IAnalysisSession analysisSession = AnalysisSession.createWithCurrentClassPath();
        IMutabilityCheckerFactory checkerFactory = new MutabilityCheckerFactory();
        ICheckerRunnerFactory checkerRunnerFactory = new CheckerRunnerFactory(null);
        AllChecksRunner checker = new AllChecksRunner(checkerFactory, checkerRunnerFactory, fromClass(immutableClass));

        checker.runCheckers(analysisSession);

        AnalysisResult result = analysisSession.resultFor(immutableClass.getCanonicalName());
        assertImmutable(result);
    }

    @Test
    public void analysisWillBeRunForClassesWhenQueriedOnImmutableStatus() throws Exception {
        IAnalysisSession analysisSession = AnalysisSession.createWithCurrentClassPath();
        AnalysisResult result = analysisSession.resultFor(immutableClass.getCanonicalName());
        assertImmutable(result);
    }
    
    @Ignore
    @Test
    public void reRequestingAnAnalysisBeforeCompleteWillReturnACircularReferenceResult() throws Exception {
        IAnalysisSession analysisSession = AnalysisSession.createWithCurrentClassPath();
        AnalysisResult result = analysisSession.resultFor(ImmutableClassA.class.getCanonicalName());
        
        assertThat(result.isImmutable, is(NOT_IMMUTABLE));
        
        MutableReasonDetail reasonDetail = result.reasons.iterator().next();
        assertThat(reasonDetail.message(), containsString("Circular reference"));
        assertTrue(reasonDetail.reason().isOneOf(MutabilityReason.CANNOT_ANALYSE));
    }

}
