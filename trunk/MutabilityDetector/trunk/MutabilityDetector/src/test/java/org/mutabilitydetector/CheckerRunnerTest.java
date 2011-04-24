package org.mutabilitydetector;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mutabilitydetector.locations.Dotted.fromClass;

import org.junit.Test;
import org.mockito.Mockito;
import org.mutabilitydetector.checkers.IMutabilityChecker;


public class CheckerRunnerTest {

    @Test public void willVisitAnalysisExceptionWhenAnUnhandledExceptionIsThrown() {
        IMutabilityChecker checker = Mockito.mock(IMutabilityChecker.class);
        
        Throwable toBeThrown = new NoSuchMethodError();
        doThrow(toBeThrown).when(checker).visit(anyInt(), anyInt(), anyString(), 
                                                anyString(), anyString(), new String[] { anyString() });
        
        CheckerRunner checkerRunner = CheckerRunner.createWithCurrentClasspath();
        
        checkerRunner.run(new AnalysisSession(), checker, fromClass(CheckerRunner.class));
        
        verify(checker).visitAnalysisException(toBeThrown);
    }
    
}
