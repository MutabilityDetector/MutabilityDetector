package org.mutabilitydetector.checkers.info;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mutabilitydetector.AnalysisResult.analysisResult;
import static org.mutabilitydetector.IAnalysisSession.RequestedAnalysis.complete;
import static org.mutabilitydetector.IsImmutable.EFFECTIVELY_IMMUTABLE;
import static org.mutabilitydetector.MutabilityReason.NON_FINAL_FIELD;
import static org.mutabilitydetector.MutableReasonDetail.newMutableReasonDetail;
import static org.mutabilitydetector.locations.ClassLocation.from;
import static org.mutabilitydetector.locations.Dotted.dotted;

import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.IAnalysisSession;
import org.mutabilitydetector.IAnalysisSession.RequestedAnalysis;
import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.locations.CodeLocation;

public class MutableTypeInformationTest {

    private final CodeLocation<?> unusedCodeLocation = from(dotted("a.b.c.D"));
    private final IAnalysisSession session = mock(IAnalysisSession.class);
    
    @Test
    public void returnsIsImmutableResultFromAnalysisSession() throws Exception {
        IsImmutable isImmutableResult = EFFECTIVELY_IMMUTABLE;
        
        AnalysisResult result = analysisResult("a.b.c.D", 
                                               isImmutableResult, 
                                               asList(newMutableReasonDetail("message", unusedCodeLocation, NON_FINAL_FIELD)));
        when(session.resultFor("a.b.c.D")).thenReturn(complete(result));
        
        MutableTypeInformation information = new MutableTypeInformation(session);
        
        assertThat(information.resultOf(dotted("a.b.c.D")).result.isImmutable, is(isImmutableResult));
        assertThat(information.resultOf(dotted("a.b.c.D")).analysisComplete, is(true));
    }
    
    @Test
    public void isNotImmutableWithCircularReferenceReasonWhenResultIsRequestedMultipleTimesAndAnalysisSessionHasNoResult() {
        when(session.resultFor("a.b.c.D")).thenReturn(RequestedAnalysis.incomplete());
        
        MutableTypeInformation information = new MutableTypeInformation(session);
        
        assertThat(information.resultOf(dotted("a.b.c.D")).analysisComplete, is(false));
        assertThat(information.resultOf(dotted("a.b.c.D")).result, is(nullValue()));
    }
    
}
