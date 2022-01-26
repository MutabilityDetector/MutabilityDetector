package org.mutabilitydetector.checkers.info;

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


import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.Configuration;
import org.mutabilitydetector.ConfigurationBuilder;
import org.mutabilitydetector.Configurations;
import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.checkers.info.MutableTypeInformation.MutabilityLookup;
import org.mutabilitydetector.locations.CodeLocation;
import org.mutabilitydetector.locations.Dotted;

import java.util.Collections;

import static java.util.Collections.singleton;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mutabilitydetector.AnalysisResult.analysisResult;
import static org.mutabilitydetector.Configurations.NO_CONFIGURATION;
import static org.mutabilitydetector.IsImmutable.EFFECTIVELY_IMMUTABLE;
import static org.mutabilitydetector.MutabilityReason.NON_FINAL_FIELD;
import static org.mutabilitydetector.MutableReasonDetail.newMutableReasonDetail;
import static org.mutabilitydetector.checkers.info.AnalysisInProgress.noAnalysisUnderway;
import static org.mutabilitydetector.locations.CodeLocation.ClassLocation.from;
import static org.mutabilitydetector.locations.Dotted.dotted;


public class MutableTypeInformationTest {

    private final Dotted mutabilityAskedOnBehalfOf = dotted("something.with.PotentiallyMutableField");

    private final Dotted needToKnowMutabilityOf = dotted("a.b.c.D");
    private final CodeLocation<?> unusedCodeLocation = from(needToKnowMutabilityOf);
    private final AnalysisSession session = mock(AnalysisSession.class);
    private final AnalysisInProgress NO_ANALYSIS_IN_PROGRESS = noAnalysisUnderway();

    private final AnalysisResult result = analysisResult(needToKnowMutabilityOf.asString(),
            EFFECTIVELY_IMMUTABLE,
            singleton(newMutableReasonDetail("message", unusedCodeLocation, NON_FINAL_FIELD)));

    @Test
    public void returnsIsImmutableResultFromAnalysisSessionIfAvailable() throws Exception {
        when(session.resultsByClass()).thenReturn(ImmutableMap.of(needToKnowMutabilityOf, result));

        MutableTypeInformation information = new MutableTypeInformation(session, NO_CONFIGURATION, CyclicReferences.newEmptyMutableInstance());

        MutabilityLookup mutabilityLookup = information.resultOf(mutabilityAskedOnBehalfOf, needToKnowMutabilityOf, NO_ANALYSIS_IN_PROGRESS);
        assertThat(mutabilityLookup.result, is(result));
        assertThat(mutabilityLookup.foundCyclicReference, is(false));
        verify(session, never()).processTransitiveAnalysis(any(Dotted.class), any(AnalysisInProgress.class));
    }

    @Test
    public void returnsKnownCyclicReferenceWhenRevisitingClassBeforeInProgressAnalysisHasComplete() {
        AnalysisInProgress analysisInProgress = noAnalysisUnderway().analysisStartedFor(needToKnowMutabilityOf).analysisStartedFor(dotted("e.f.g.H"));

        MutableTypeInformation information = new MutableTypeInformation(session, NO_CONFIGURATION, CyclicReferences.newEmptyMutableInstance());

        assertThat(information.resultOf(mutabilityAskedOnBehalfOf, needToKnowMutabilityOf, analysisInProgress).foundCyclicReference, is(true));
    }

    @Test
    public void returnsKnownCyclicReferenceIfRequestingAnalysisOfFieldThatIsSameTypeAsCurrentAnalysisInProgress() {
        AnalysisInProgress analysisInProgress = noAnalysisUnderway();

        MutableTypeInformation information = new MutableTypeInformation(session, NO_CONFIGURATION, CyclicReferences.newEmptyMutableInstance());

        assertThat(information.resultOf(needToKnowMutabilityOf, needToKnowMutabilityOf, analysisInProgress).foundCyclicReference, is(true));
    }

    @Test
    public void startsAnalysisOfFieldTypeWhenItIsNotAlreadyAvailableAndDoesNotIndicateCyclicReference() {
        AnalysisInProgress analysisInProgress = noAnalysisUnderway();

        when(session.resultsByClass()).thenReturn(Collections.<Dotted, AnalysisResult>emptyMap());
        when(session.processTransitiveAnalysis(needToKnowMutabilityOf, analysisInProgress.analysisStartedFor(mutabilityAskedOnBehalfOf))).thenReturn(result);

        MutableTypeInformation information = new MutableTypeInformation(session, NO_CONFIGURATION, CyclicReferences.newEmptyMutableInstance());

        MutabilityLookup mutabilityLookup = information.resultOf(mutabilityAskedOnBehalfOf, needToKnowMutabilityOf, analysisInProgress);
        assertThat(mutabilityLookup.result, is(result));
        assertThat(mutabilityLookup.foundCyclicReference, is(false));
    }
    
    @Test
    public void canConfigureAnalysisSessionToHardcodeResultForClass() throws Exception {
        final AnalysisResult harcodedResult = AnalysisResult.analysisResult("some.type.i.say.is.Immutable", IsImmutable.IMMUTABLE);
        Configuration configuration = new ConfigurationBuilder() {
            @Override public void configure() {
                hardcodeResult(harcodedResult);
            }
        }.build();
        MutableTypeInformation information = new MutableTypeInformation(session, configuration, CyclicReferences.newEmptyMutableInstance());
        
        assertThat(information.resultOf(mutabilityAskedOnBehalfOf, dotted("some.type.i.say.is.Immutable"), NO_ANALYSIS_IN_PROGRESS).result,
                sameInstance(harcodedResult));
    }
    
}
