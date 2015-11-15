package org.mutabilitydetector.checkers;

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


import com.google.common.base.Throwables;
import org.junit.Test;
import org.mutabilitydetector.AnalysisError;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.benchmarks.ImmutableExample;
import org.mutabilitydetector.checkers.CheckerRunner.ExceptionPolicy;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mutabilitydetector.checkers.CheckerRunner.ExceptionPolicy.FAIL_FAST;
import static org.mutabilitydetector.locations.Dotted.fromClass;

public class CheckerRunnerTest {

    @Test
    public void willPropagateAnExceptionWhenConfiguredToFailFast() {
        Throwable toBeThrown = new NoSuchMethodError();
        AsmMutabilityChecker checker = checkerWhichThrows(toBeThrown);
        
        CheckerRunner checkerRunner = CheckerRunner.createWithCurrentClasspath(FAIL_FAST);
        
        try {
            checkerRunner.run(checker, fromClass(CheckerRunner.class), Collections.<AnalysisResult>emptyList());
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
        checkerRunner.run(checker, fromClass(CheckerRunner.class), Collections.<AnalysisResult>emptyList());
    }

    @Test
    public void willPropagateUnrecoverableExceptions() throws Exception {
        Throwable toBeThrown = new OutOfMemoryError();
        AsmMutabilityChecker checker = checkerWhichThrows(toBeThrown);
        
        CheckerRunner checkerRunner = CheckerRunner.createWithCurrentClasspath(ExceptionPolicy.CARRY_ON);
        
        try {
            checkerRunner.run(checker, fromClass(CheckerRunner.class), Collections.<AnalysisResult>emptyList());
            fail("expected exception");
        } catch (MutabilityAnalysisException expected) {
            assertSame(toBeThrown, expected.getCause());
        }
    }

    @Test
    public void attemptsToRecoverFromNoClassDefFoundError() throws Exception {
        checkCapturesErrorWithoutThrowingException(new NoClassDefFoundError());
    }

    @Test
    public void attemptsToRecoverFromErrorCausedByNoClassDefFoundError() throws Exception {
        Throwable rootCause = new NoClassDefFoundError();
        Throwable toBeThrown = new Error();
        toBeThrown.initCause(rootCause);

        checkCapturesErrorWithoutThrowingException(toBeThrown);
    }

    @Test
    public void attemptsToRecoverFromAnyLinkageError() throws Exception {
        Throwable rootCause = new UnsatisfiedLinkError();
        Throwable toBeThrown = new Error();
        toBeThrown.initCause(rootCause);

        checkCapturesErrorWithoutThrowingException(toBeThrown);
    }

    private void checkCapturesErrorWithoutThrowingException(Throwable toBeThrown) {
        AsmMutabilityChecker checker = checkerWhichThrows(toBeThrown);

        CheckerRunner checkerRunner = CheckerRunner.createWithCurrentClasspath(ExceptionPolicy.CARRY_ON);
        CheckerResult result = checkerRunner.run(checker, fromClass(CheckerRunner.class), Collections.<AnalysisResult>emptyList());

        assertThat(result.errors, hasSize(1));
    }

    @Test
    public void checkerReturnsAnalysisErrorsWhenEncountered() {
        Throwable rootCause = new UnsatisfiedLinkError();
        Throwable toBeThrown = new Error();
        toBeThrown.initCause(rootCause);

        AsmMutabilityChecker checker = checkerWhichThrows(toBeThrown);

        CheckerRunner checkerRunner = CheckerRunner.createWithCurrentClasspath(ExceptionPolicy.CARRY_ON);
        CheckerResult result = checkerRunner.run(checker, fromClass(ImmutableExample.class), Collections.<AnalysisResult>emptyList());

        assertThat(result.errors, hasSize(1));

        assertThat(result.isImmutable, is(IsImmutable.COULD_NOT_ANALYSE));
        assertThat(result.reasons.iterator().next().message(), is("Encountered an unhandled error in analysis."));

        AnalysisError error = result.errors.iterator().next();

        assertThat(error.checkerName, is("ExceptionThrowingMutabilityChecker"));
        assertThat(error.description,
                containsString("It is likely that the class org.mutabilitydetector.benchmarks.ImmutableExample has " +
                        "dependencies outwith the given class path."));
    }

    private AsmMutabilityChecker checkerWhichThrows(Throwable toBeThrown) {
        return new ExceptionThrowingMutabilityChecker(toBeThrown);
    }

    private static class ExceptionThrowingMutabilityChecker extends AsmMutabilityChecker {

        private final Throwable willThrow;

        ExceptionThrowingMutabilityChecker(Throwable willThrow) {
            this.willThrow = willThrow;
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            Throwables.propagate(willThrow);
        }
    }

}
