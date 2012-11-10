package org.mutabilitydetector.cli;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Mockito.verify;
import static org.mutabilitydetector.locations.Dotted.dotted;

import org.junit.Test;
import org.mockito.Mockito;
import org.mutabilitydetector.AnalysisSession;

public class BatchAnalysisSessionTest {

    @Test
    public void triggersAnalysisForEachGivenClass() throws Exception {
        AnalysisSession underlyingSession = Mockito.mock(AnalysisSession.class);
        
        BatchAnalysisSession batchAnalysisSession = new BatchAnalysisSession(underlyingSession);
        
        batchAnalysisSession.runAnalysis(newArrayList(dotted("a.b.C"), dotted("d.e.F")));
        
        verify(underlyingSession).resultFor(dotted("a.b.C"));
        verify(underlyingSession).resultFor(dotted("d.e.F"));
    }
    
}
