package org.mutabilitydetector.checkers;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.IAnalysisSession;
import org.mutabilitydetector.UnhandledExceptionBuilder;
import org.mutabilitydetector.locations.Dotted;

public class UnhandledExceptionBuilderTest {

    private final UnhandledExceptionBuilder exceptionBuilder = new UnhandledExceptionBuilder();
    
    
    private final Throwable unusedCause = new NullPointerException();
    private final IAnalysisSession unusedAnalysisSession = mock(IAnalysisSession.class);
    private final AsmMutabilityChecker unusedChecker = mock(AsmMutabilityChecker.class);
    private final Dotted unusedClass = Dotted.dotted("unus.ed");
    
    @Test
    public void exceptionCreatedHasGivenExceptionAsCause() throws Exception {
        Exception cause = new NullPointerException();
        MutabilityAnalysisException unhandledException = 
                exceptionBuilder.unhandledException(cause, unusedAnalysisSession, unusedChecker, unusedClass);
        
        assertSame(cause, unhandledException.getCause());
    }
    
    @Test
    public void messageOfExceptionContainsUsefulInformationForEndUser() throws Exception {
        MutabilityAnalysisException unhandledException = 
                exceptionBuilder.unhandledException(unusedCause, unusedAnalysisSession, unusedChecker, unusedClass);
        
        assertThat(unhandledException.getMessage(), 
                   allOf(containsString("sorry"),
                         containsString("An unhandled error occurred"), 
                         containsString("http://code.google.com/p/mutability-detector/issues/list")));
    }
    
    @Test
    public void messageOfExceptionContainsUsefulInformationForDeveloper_whichCheckerFailed() throws Exception {
        AsmMutabilityChecker checkerThatFailed = new NullMutabilityChecker();
        MutabilityAnalysisException unhandledException = 
                exceptionBuilder.unhandledException(unusedCause, unusedAnalysisSession, checkerThatFailed, unusedClass);
        
        assertThat(unhandledException.getMessage(), 
                   containsString("\nChecker that failed: NullMutabilityChecker\n"));
    }
    
    @Test
    public void messageOfExceptionContainsUsefulInformationForDeveloper_classBeingAnalysed() throws Exception {
        Dotted classBeingAnalysed = Dotted.dotted("this.is.the.class.being.Analysed");
        MutabilityAnalysisException unhandledException = 
                exceptionBuilder.unhandledException(unusedCause, unusedAnalysisSession, unusedChecker, classBeingAnalysed);
        
        assertThat(unhandledException.getMessage(), 
                   containsString("\nClass being analysed: this.is.the.class.being.Analysed\n"));
    }
    
    @Test
    public void messageOfExceptionContainsUsefulInformationForDeveloper_fromAnalysisSession() throws Exception {
        IAnalysisSession analysisSession = mock(IAnalysisSession.class);
        
        AnalysisResult first = AnalysisResult.definitelyImmutable("a.b.c");
        AnalysisResult second = AnalysisResult.definitelyImmutable("e.f.g");
        AnalysisResult third = AnalysisResult.definitelyImmutable("h.i.j");
        
        when(analysisSession.getResults()).thenReturn(asList(first, second, third));
        
        MutabilityAnalysisException unhandledException = 
                exceptionBuilder.unhandledException(unusedCause, analysisSession, unusedChecker, unusedClass);
        
        assertThat(unhandledException.getMessage(), 
                   containsString("\nClasses analysed so far:\n    a.b.c\n    e.f.g\n    h.i.j\n"));
    }
    
}
