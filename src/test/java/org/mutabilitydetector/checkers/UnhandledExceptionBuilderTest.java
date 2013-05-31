package org.mutabilitydetector.checkers;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

import java.util.Collections;

import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.UnhandledExceptionBuilder;
import org.mutabilitydetector.locations.Dotted;

public class UnhandledExceptionBuilderTest {

    private final UnhandledExceptionBuilder exceptionBuilder = new UnhandledExceptionBuilder();
    
    private final Throwable unusedCause = new NullPointerException();
    private final AsmMutabilityChecker unusedChecker = mock(AsmMutabilityChecker.class);
    private final Dotted unusedClass = Dotted.dotted("unus.ed");
    
    private final Iterable<AnalysisResult> noResultsSoFar = Collections.<AnalysisResult>emptyList();

    @Test
    public void exceptionCreatedHasGivenExceptionAsCause() throws Exception {
        Exception cause = new NullPointerException();
        MutabilityAnalysisException unhandledException = 
                exceptionBuilder.unhandledException(cause, noResultsSoFar, unusedChecker, unusedClass);
        
        assertSame(cause, unhandledException.getCause());
    }
    
    @Test
    public void messageOfExceptionContainsUsefulInformationForEndUser() throws Exception {
        MutabilityAnalysisException unhandledException = 
                exceptionBuilder.unhandledException(unusedCause, noResultsSoFar, unusedChecker, unusedClass);
        
        assertThat(unhandledException.getMessage(), 
                   allOf(containsString("sorry"),
                         containsString("An unhandled error occurred"), 
                         containsString("http://code.google.com/p/mutability-detector/issues/list")));
    }
    
    @Test
    public void messageOfExceptionContainsUsefulInformationForDeveloper_whichCheckerFailed() throws Exception {
        AsmMutabilityChecker checkerThatFailed = new NullMutabilityChecker();
        MutabilityAnalysisException unhandledException = 
                exceptionBuilder.unhandledException(unusedCause, noResultsSoFar, checkerThatFailed, unusedClass);
        
        assertThat(unhandledException.getMessage(), 
                   containsString("\nChecker that failed: NullMutabilityChecker\n"));
    }
    
    @Test
    public void messageOfExceptionContainsUsefulInformationForDeveloper_classBeingAnalysed() throws Exception {
        Dotted classBeingAnalysed = Dotted.dotted("this.is.the.clazz.being.Analysed");
        MutabilityAnalysisException unhandledException = 
                exceptionBuilder.unhandledException(unusedCause, noResultsSoFar, unusedChecker, classBeingAnalysed);
        
        assertThat(unhandledException.getMessage(), 
                   containsString("\nClass being analysed: this.is.the.clazz.being.Analysed\n"));
    }
    
    @Test
    public void messageOfExceptionContainsUsefulInformationForDeveloper_fromAnalysisSession() throws Exception {
        AnalysisResult first = AnalysisResult.definitelyImmutable("a.b.c");
        AnalysisResult second = AnalysisResult.definitelyImmutable("e.f.g");
        AnalysisResult third = AnalysisResult.definitelyImmutable("h.i.j");
        
        MutabilityAnalysisException unhandledException = 
                exceptionBuilder.unhandledException(unusedCause, asList(first, second, third), unusedChecker, unusedClass);
        
        assertThat(unhandledException.getMessage(), 
                   containsString("\nClasses analysed so far:\n    a.b.c\n    e.f.g\n    h.i.j\n"));
    }
    
}
