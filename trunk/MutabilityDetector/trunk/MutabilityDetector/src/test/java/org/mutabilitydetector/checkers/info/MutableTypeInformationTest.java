package org.mutabilitydetector.checkers.info;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mutabilitydetector.AnalysisResult.analysisResult;
import static org.mutabilitydetector.DefaultConfiguration.NO_CONFIGURATION;
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
import org.mutabilitydetector.DefaultConfiguration;
import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.checkers.info.MutableTypeInformation.CircularReference;
import org.mutabilitydetector.locations.CodeLocation;
import org.mutabilitydetector.locations.Dotted;

import com.google.common.collect.Sets;

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
        
        MutableTypeInformation information = new MutableTypeInformation(session, NO_CONFIGURATION);
        
        assertThat(information.resultOf(mutabilityAskedOnBehalfOf, needToKnowMutabilityOf).result.isImmutable, is(isImmutableResult));
        assertThat(information.resultOf(mutabilityAskedOnBehalfOf, needToKnowMutabilityOf).foundCyclicReference, is(false));
    }
    
    @Test
    public void canConfigureAnalysisSessionToHardcodeResultForClass() throws Exception {
        AnalysisResult harcodedResult = AnalysisResult.analysisResult("some.type.i.say.is.Immutable", IsImmutable.IMMUTABLE);
        Configuration configuration = new DefaultConfiguration(Sets.newHashSet(harcodedResult));
        MutableTypeInformation information = new MutableTypeInformation(session, configuration);
        
        assertThat(information.resultOf(mutabilityAskedOnBehalfOf, dotted("some.type.i.say.is.Immutable")).result, 
                sameInstance(harcodedResult));
    }
    
    @Test
    public void circularReferenceIsEqualsWhenBothTypesAreIncluded() throws Exception {
        assertEquals(new CircularReference(dotted("a.b.C"), dotted("d.e.F")), new CircularReference(dotted("a.b.C"), dotted("d.e.F")));
        assertEquals(new CircularReference(dotted("a.b.C"), dotted("d.e.F")), new CircularReference(dotted("d.e.F"), dotted("a.b.C")));
        assertFalse(new CircularReference(dotted("a.b.C"), dotted("d.e.F")).equals(new CircularReference(dotted("d.e.F"), dotted("g.h.I"))));
    }
    
}
