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

import static java.lang.String.format;
import static org.mutabilitydetector.CheckerRunner.ExceptionPolicy.FAIL_FAST;

import java.io.IOException;
import java.io.InputStream;

import org.mutabilitydetector.AnalysisErrorReporter.AnalysisError;
import org.mutabilitydetector.checkers.AsmMutabilityChecker;
import org.mutabilitydetector.checkers.AsmMutabilityChecker.CheckerResult;
import org.mutabilitydetector.locations.Dotted;
import org.objectweb.asm.ClassReader;

import com.google.classpath.ClassPath;
import com.google.classpath.ClassPathFactory;

public final class CheckerRunner {

    private final ClassPath classpath;
    private final UnhandledExceptionBuilder unhandledExceptionBuilder;
    private ExceptionPolicy exceptionPolicy;

    private CheckerRunner(ClassPath classpath, UnhandledExceptionBuilder unhandledExceptionBuilder, ExceptionPolicy exceptionPolicy) {
        this.classpath = classpath;
        this.unhandledExceptionBuilder = unhandledExceptionBuilder;
        this.exceptionPolicy = exceptionPolicy;
    }

    public static CheckerRunner createWithClasspath(ClassPath classpath, ExceptionPolicy exceptionPolicy) {
        return new CheckerRunner(classpath, new UnhandledExceptionBuilder(), exceptionPolicy);
    }

    public static CheckerRunner createWithCurrentClasspath(ExceptionPolicy exceptionPolicy) {
        return createWithClasspath(new ClassPathFactory().createFromJVM(), exceptionPolicy);
    }
    
    public static enum ExceptionPolicy {
        FAIL_FAST, CARRY_ON
    }

    public CheckerResult run(AsmMutabilityChecker checker, Dotted className, AnalysisErrorReporter errorReporter, Iterable<AnalysisResult> resultsSoFar) {
        try {
            try {
                analyseAsStream(checker, className.asString());
            } catch (Exception e) {
                analyseFromDefaultClassLoader(checker, className.asString());
            }
        } catch (Throwable e) {
            attemptRecovery(checker, className, errorReporter, resultsSoFar, e);
        }
        return checker.checkerResult();
    }


    private void attemptRecovery(AsmMutabilityChecker checker,
                                 Dotted className,
                                 AnalysisErrorReporter errorReporter,
                                 Iterable<AnalysisResult> resultsSoFar,
                                 Throwable e) {

        if (!isRecoverable(e) || exceptionPolicy == FAIL_FAST) {
            throw unhandledExceptionBuilder.unhandledException(e, resultsSoFar, checker, className);
        } else {
            handleException(errorReporter, checker, className.asString(), e);
        }
    }
    
    private boolean isRecoverable(Throwable e) {
        Throwable cause = underlyingCause(e);
        return (cause instanceof Exception) || (cause instanceof LinkageError);
    }

    private Throwable underlyingCause(Throwable e) {
        Throwable rootCause = e;
        while (rootCause.getCause() != null) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }

    private void analyseFromDefaultClassLoader(AsmMutabilityChecker checker, String className) throws IOException {
        ClassReader cr = new ClassReader(className);
        cr.accept(checker, 0);
    }

    private void analyseAsStream(AsmMutabilityChecker checker, String dottedClassPath) throws IOException {
        String slashedClassPath = dottedClassPath.replace(".", "/").concat(".class");
        InputStream classStream = classpath.getResourceAsStream(slashedClassPath);
        ClassReader cr = new ClassReader(classStream);
        cr.accept(checker, 0);
    }

    private void handleException(AnalysisErrorReporter errorReporter, AsmMutabilityChecker checker, String dottedClassPath, Throwable e) {
        String errorDescription = createErrorDescription(dottedClassPath);
        checker.visitAnalysisException(e);
        AnalysisError error = new AnalysisError(dottedClassPath, getNameOfChecker(checker), errorDescription);
        errorReporter.addAnalysisError(error);
    }

    public String createErrorDescription(String dottedClassPath) {
        return format("It is likely that the class %s has dependencies outwith the given class path.", dottedClassPath);
    }

    private String getNameOfChecker(AsmMutabilityChecker checker) {
        String checkerName = checker.getClass().getName();
        return checkerName.substring(checkerName.lastIndexOf(".") + 1);
    }
}
