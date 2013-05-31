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
import org.mutabilitydetector.locations.Dotted;

public class UnhandledExceptionBuilderTest {

    private final UnhandledExceptionBuilder exceptionBuilder = new UnhandledExceptionBuilder();
    
    private final Throwable unusedCause = new NullPointerException();
    private final AsmMutabilityChecker unusedChecker = mock(AsmMutabilityChecker.class);
    private final Dotted unusedClass = Dotted.dotted("unus.ed");
    
    private final String newline = System.getProperty("line.separator");
    
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
                         containsString("https://github.com/MutabilityDetector/MutabilityDetector/issues/")));
    }
    
    @Test
    public void messageOfExceptionContainsUsefulInformationForDeveloper_whichCheckerFailed() throws Exception {
        AsmMutabilityChecker checkerThatFailed = new NullMutabilityChecker();
        MutabilityAnalysisException unhandledException = 
                exceptionBuilder.unhandledException(unusedCause, noResultsSoFar, checkerThatFailed, unusedClass);
        
        assertThat(unhandledException.getMessage(), 
                   containsString(newline + "Checker that failed: NullMutabilityChecker" + newline));
    }
    
    @Test
    public void messageOfExceptionContainsUsefulInformationForDeveloper_classBeingAnalysed() throws Exception {
        Dotted classBeingAnalysed = Dotted.dotted("this.is.the.clazz.being.Analysed");
        MutabilityAnalysisException unhandledException = 
                exceptionBuilder.unhandledException(unusedCause, noResultsSoFar, unusedChecker, classBeingAnalysed);
        
        assertThat(unhandledException.getMessage(), 
                   containsString(newline + "Class being analysed: this.is.the.clazz.being.Analysed" + newline));
    }
    
    @Test
    public void messageOfExceptionContainsUsefulInformationForDeveloper_fromAnalysisSession() throws Exception {
        AnalysisResult first = AnalysisResult.definitelyImmutable("a.b.c");
        AnalysisResult second = AnalysisResult.definitelyImmutable("e.f.g");
        AnalysisResult third = AnalysisResult.definitelyImmutable("h.i.j");
        
        MutabilityAnalysisException unhandledException = 
                exceptionBuilder.unhandledException(unusedCause, asList(first, second, third), unusedChecker, unusedClass);
        
        assertThat(unhandledException.getMessage(), 
                   containsString(newline + "Classes analysed so far:" +
                                  newline + "    a.b.c" +
                                  newline + "    e.f.g" +
                                  newline + "    h.i.j" + newline));
    }
    
}
