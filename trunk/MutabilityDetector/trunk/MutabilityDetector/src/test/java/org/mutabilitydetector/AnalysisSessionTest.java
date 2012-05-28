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
import static org.mutabilitydetector.TestUtil.testingAnalysisClassLoader;
import static org.mutabilitydetector.locations.Dotted.fromClass;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mutabilitydetector.benchmarks.ImmutableExample;

import com.google.common.collect.Sets;

public class AnalysisSessionTest {

    private Class<ImmutableExample> immutableClass;
    private AnalysisClassLoader fallbackClassLoader = testingAnalysisClassLoader();

    @Before
    public void setUp() {
        immutableClass = ImmutableExample.class;
    }

    @Test
    public void analysisOfImmutableExampleWillBeRegistered() throws Exception {
        IAnalysisSession analysisSession = AnalysisSession.createWithCurrentClassPath();
        IMutabilityCheckerFactory checkerFactory = new MutabilityCheckerFactory();
        ICheckerRunnerFactory checkerRunnerFactory = new CheckerRunnerFactory(null);
        AllChecksRunner checker = new AllChecksRunner(checkerFactory, checkerRunnerFactory, fromClass(immutableClass), fallbackClassLoader);

        checker.runCheckers(analysisSession);

        AnalysisResult result = analysisSession.resultFor(immutableClass.getCanonicalName()).result;
        assertThat(result, areImmutable());
    }

    @Test
    public void analysisWillBeRunForClassesWhenQueriedOnImmutableStatus() throws Exception {
        IAnalysisSession analysisSession = AnalysisSession.createWithCurrentClassPath();
        AnalysisResult result = analysisSession.resultFor(immutableClass.getCanonicalName()).result;
        assertThat(result, areImmutable());
    }
    
    @Test
	public void canConfigureAnalysisSessionToHardcodeResultForClass() throws Exception {
    	Set<AnalysisResult> predefinedResults = Sets.newHashSet(AnalysisResult.analysisResult("some.type.i.say.is.Immutable", IsImmutable.IMMUTABLE));
    	
    	Configuration configuration = new Configuration(predefinedResults);
		IAnalysisSession analysisSession = AnalysisSession.createWithCurrentClassPath(configuration);
		AnalysisResult result = analysisSession.resultFor("some.type.i.say.is.Immutable").result;
		
		assertThat(result, areImmutable());
	}
    
}
