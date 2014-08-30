package org.mutabilitydetector.cli;

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
