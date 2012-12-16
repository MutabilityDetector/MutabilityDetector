/*
 *    Copyright (c) 2008-2011 Graham Allan
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package org.mutabilitydetector;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mutabilitydetector.CheckerRunner.ExceptionPolicy.FAIL_FAST;
import static org.mutabilitydetector.locations.Dotted.fromClass;

import java.util.Collections;

import org.junit.Test;
import org.mutabilitydetector.CheckerRunner.ExceptionPolicy;
import org.mutabilitydetector.checkers.AsmMutabilityChecker;
import org.mutabilitydetector.checkers.MutabilityAnalysisException;

public class CheckerRunnerTest {

    @Test
    public void willPropagateAnExceptionWhenConfiguredToFailFast() {
        Throwable toBeThrown = new NoSuchMethodError();
        AsmMutabilityChecker checker = checkerWhichThrows(toBeThrown);
        
        CheckerRunner checkerRunner = CheckerRunner.createWithCurrentClasspath(FAIL_FAST);
        
        try {
            AnalysisSession analysisSession = TestUtil.testAnalysisSession();
            checkerRunner.run(checker, fromClass(CheckerRunner.class), analysisSession.errorReporter(), Collections.<AnalysisResult>emptyList());
            fail("expected exception");
        } catch (MutabilityAnalysisException expected) {
            assertSame(toBeThrown, expected.getCause());
        }
    }
    
    @Test
    public void willNotPropagateAnExceptionWhenConfiguredToCarryOn() throws Exception {
        Throwable toBeThrown = new NullPointerException();
        AsmMutabilityChecker checker = checkerWhichThrows(toBeThrown);

        CheckerRunner checkerRunner = CheckerRunner.createWithCurrentClasspath(ExceptionPolicy.CARRY_ON);

        AnalysisSession analysisSession = TestUtil.testAnalysisSession();
        checkerRunner.run(checker, fromClass(CheckerRunner.class), analysisSession.errorReporter(), Collections.<AnalysisResult>emptyList());

        verify(checker, atLeastOnce()).visitAnalysisException(toBeThrown);
    }

    @Test
    public void willPropagateUnrecoverableExceptions() throws Exception {
        Throwable toBeThrown = new OutOfMemoryError();
        AsmMutabilityChecker checker = checkerWhichThrows(toBeThrown);
        
        CheckerRunner checkerRunner = CheckerRunner.createWithCurrentClasspath(ExceptionPolicy.CARRY_ON);
        
        try {
            AnalysisSession analysisSession = TestUtil.testAnalysisSession();
            checkerRunner.run(checker, fromClass(CheckerRunner.class), analysisSession.errorReporter(), Collections.<AnalysisResult>emptyList());
            fail("expected exception");
        } catch (MutabilityAnalysisException expected) {
            assertSame(toBeThrown, expected.getCause());
        }
    }

    @Test
    public void attemptsToRecoverFromNoClassDefFoundError() throws Exception {
        Throwable toBeThrown = new NoClassDefFoundError();
        AsmMutabilityChecker checker = checkerWhichThrows(toBeThrown);
        
        CheckerRunner checkerRunner = CheckerRunner.createWithCurrentClasspath(ExceptionPolicy.CARRY_ON);
        AnalysisSession analysisSession = TestUtil.testAnalysisSession();
        checkerRunner.run(checker, fromClass(CheckerRunner.class), analysisSession.errorReporter(), Collections.<AnalysisResult>emptyList());
            
        verify(checker, atLeastOnce()).visitAnalysisException(toBeThrown);
    }

    @Test
    public void attemptsToRecoverFromErrorCausedByNoClassDefFoundError() throws Exception {
        Throwable rootCause = new NoClassDefFoundError();
        Throwable toBeThrown = new Error();
        toBeThrown.initCause(rootCause);
        
        AsmMutabilityChecker checker = checkerWhichThrows(toBeThrown);
        
        CheckerRunner checkerRunner = CheckerRunner.createWithCurrentClasspath(ExceptionPolicy.CARRY_ON);
        AnalysisSession analysisSession = TestUtil.testAnalysisSession();
        checkerRunner.run(checker, fromClass(CheckerRunner.class), analysisSession.errorReporter(), Collections.<AnalysisResult>emptyList());
        
        verify(checker, atLeastOnce()).visitAnalysisException(toBeThrown);
    }

    @Test
    public void attemptsToRecoverFromAnyLinkageError() throws Exception {
        Throwable rootCause = new UnsatisfiedLinkError();
        Throwable toBeThrown = new Error();
        toBeThrown.initCause(rootCause);
        
        AsmMutabilityChecker checker = checkerWhichThrows(toBeThrown);
        
        CheckerRunner checkerRunner = CheckerRunner.createWithCurrentClasspath(ExceptionPolicy.CARRY_ON);
        AnalysisSession analysisSession = TestUtil.testAnalysisSession();
        checkerRunner.run(checker, fromClass(CheckerRunner.class), analysisSession.errorReporter(), Collections.<AnalysisResult>emptyList());
        
        verify(checker, atLeastOnce()).visitAnalysisException(toBeThrown);
    }

    private AsmMutabilityChecker checkerWhichThrows(Throwable toBeThrown) {
        AsmMutabilityChecker checker = mock(AsmMutabilityChecker.class);
        doThrow(toBeThrown).when(checker).visit(anyInt(),
                anyInt(),
                anyString(),
                anyString(),
                anyString(),
                new String[] { anyString() });
        return checker;
    }

}
