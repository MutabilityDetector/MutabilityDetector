/*
 *    Copyright (c) 2008-2013 Graham Allan
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
package org.mutabilitydetector.checkers.info;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mutabilitydetector.AnalysisResult.analysisResult;
import static org.mutabilitydetector.IsImmutable.EFFECTIVELY_IMMUTABLE;
import static org.mutabilitydetector.MutabilityReason.NON_FINAL_FIELD;
import static org.mutabilitydetector.MutableReasonDetail.newMutableReasonDetail;
import static org.mutabilitydetector.locations.ClassLocation.from;
import static org.mutabilitydetector.locations.Dotted.dotted;

import java.util.Collections;

import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.Configuration;
import org.mutabilitydetector.ConfigurationBuilder;
import org.mutabilitydetector.Configurations;
import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.locations.CodeLocation;
import org.mutabilitydetector.locations.Dotted;

public class MutableTypeInformationTest {

    private final Dotted mutabilityAskedOnBehalfOf = dotted("something.with.PotentiallyMutableField");

    private final Dotted needToKnowMutabilityOf = dotted("a.b.c.D");
    private final CodeLocation<?> unusedCodeLocation = from(needToKnowMutabilityOf);
    private final AnalysisSession session = mock(AnalysisSession.class);
    
    @Test
    public void returnsIsImmutableResultFromAnalysisSession() throws Exception {
        IsImmutable isImmutableResult = EFFECTIVELY_IMMUTABLE;
        
        AnalysisResult result = analysisResult("a.b.c.D", 
                                               isImmutableResult, 
                                               asList(newMutableReasonDetail("message", unusedCodeLocation, NON_FINAL_FIELD)));

        when(session.getResults()).thenReturn(Collections.<AnalysisResult>emptyList());
        when(session.resultFor(needToKnowMutabilityOf)).thenReturn(result);
        
        MutableTypeInformation information = new MutableTypeInformation(session, Configurations.NO_CONFIGURATION);
        
        assertThat(information.resultOf(mutabilityAskedOnBehalfOf, needToKnowMutabilityOf).result.isImmutable, is(isImmutableResult));
        assertThat(information.resultOf(mutabilityAskedOnBehalfOf, needToKnowMutabilityOf).foundCyclicReference, is(false));
    }
    
    @Test
    public void canConfigureAnalysisSessionToHardcodeResultForClass() throws Exception {
        final AnalysisResult harcodedResult = AnalysisResult.analysisResult("some.type.i.say.is.Immutable", IsImmutable.IMMUTABLE);
        Configuration configuration = new ConfigurationBuilder() {
            @Override public void configure() {
                hardcodeResult(harcodedResult);
            }
        }.build();
        MutableTypeInformation information = new MutableTypeInformation(session, configuration);
        
        assertThat(information.resultOf(mutabilityAskedOnBehalfOf, dotted("some.type.i.say.is.Immutable")).result, 
                sameInstance(harcodedResult));
    }
    
}
