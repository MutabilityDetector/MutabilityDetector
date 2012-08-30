package org.mutabilitydetector.checkers.info;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mutabilitydetector.AnalysisResult.analysisResult;
import static org.mutabilitydetector.AnalysisSession.RequestedAnalysis.complete;
import static org.mutabilitydetector.IsImmutable.EFFECTIVELY_IMMUTABLE;
import static org.mutabilitydetector.MutabilityReason.NON_FINAL_FIELD;
import static org.mutabilitydetector.MutableReasonDetail.newMutableReasonDetail;
import static org.mutabilitydetector.locations.ClassLocation.from;
import static org.mutabilitydetector.locations.Dotted.dotted;

import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.AnalysisSession.RequestedAnalysis;
import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.locations.CodeLocation;
import org.mutabilitydetector.locations.Dotted;

public class MutableTypeInformationTest {

    private final Dotted className = dotted("a.b.c.D");
	private final CodeLocation<?> unusedCodeLocation = from(className);
    private final AnalysisSession session = mock(AnalysisSession.class);
    
    @Test
    public void returnsIsImmutableResultFromAnalysisSession() throws Exception {
        IsImmutable isImmutableResult = EFFECTIVELY_IMMUTABLE;
        
        AnalysisResult result = analysisResult("a.b.c.D", 
                                               isImmutableResult, 
                                               asList(newMutableReasonDetail("message", unusedCodeLocation, NON_FINAL_FIELD)));
        when(session.resultFor(className)).thenReturn(complete(result));
        
        MutableTypeInformation information = new MutableTypeInformation(session);
        
        assertThat(information.resultOf(className).result.isImmutable, is(isImmutableResult));
        assertThat(information.resultOf(className).analysisComplete, is(true));
    }
    
    @Test
    public void isNotImmutableWithCircularReferenceReasonWhenResultIsRequestedMultipleTimesAndAnalysisSessionHasNoResult() {
        when(session.resultFor(className)).thenReturn(RequestedAnalysis.incomplete());
        
        MutableTypeInformation information = new MutableTypeInformation(session);
        
        assertThat(information.resultOf(className).analysisComplete, is(false));
        assertThat(information.resultOf(className).result, is(nullValue()));
    }
    
}
