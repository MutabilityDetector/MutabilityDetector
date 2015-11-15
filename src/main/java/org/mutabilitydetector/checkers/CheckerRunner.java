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



import static java.lang.String.format;
import static java.util.Collections.singleton;
import static org.mutabilitydetector.MutabilityReason.CANNOT_ANALYSE;
import static org.mutabilitydetector.MutableReasonDetail.newMutableReasonDetail;
import static org.mutabilitydetector.checkers.CheckerRunner.ExceptionPolicy.FAIL_FAST;

import java.io.IOException;
import java.io.InputStream;

import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.AnalysisError;
import org.mutabilitydetector.asmoverride.AsmClassVisitor;
import org.mutabilitydetector.locations.CodeLocation;
import org.mutabilitydetector.locations.Dotted;
import org.objectweb.asm.ClassReader;

import com.google.classpath.ClassPath;
import com.google.classpath.ClassPathFactory;
import org.objectweb.asm.ClassVisitor;

public final class CheckerRunner {

    private final ClassPath classpath;
    private final UnhandledExceptionBuilder unhandledExceptionBuilder;
    private final ExceptionPolicy exceptionPolicy;

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
    
    public enum ExceptionPolicy {
        FAIL_FAST, CARRY_ON
    }

    public CheckerResult run(AsmMutabilityChecker checker, Dotted className, Iterable<AnalysisResult> resultsSoFar) {
        try {
            try {
                analyseFromStream(checker, className);
            } catch (Exception e) {
                analyseFromClassLoader(checker, className);
            }
        } catch (Throwable e) {
            AnalysisError error = attemptRecovery(checker, className, resultsSoFar, e);

            return new CheckerResult(
                    CANNOT_ANALYSE.createsResult(),
                    singleton(newMutableReasonDetail("Encountered an unhandled error in analysis.", codeLocationOf(className), CANNOT_ANALYSE)),
                    singleton(error));
        }
        return checker.checkerResult();
    }

    public void nonMutabilityCheckerRun(AsmClassVisitor visitor, Dotted className, Iterable<AnalysisResult> resultsSoFar) {
        try {
            try {
                analyseFromStream(visitor, className);
            } catch (Exception e) {
                analyseFromClassLoader(visitor, className);
            }
        } catch (Throwable e) {
            attemptRecovery(visitor, className, resultsSoFar, e);
        }
    }

    private CodeLocation<?> codeLocationOf(Dotted className) {
        return className != null
                ? CodeLocation.ClassLocation.from(className)
                : CodeLocation.UnknownCodeLocation.UNKNOWN;
    }

    private void analyseFromStream(ClassVisitor checker, Dotted dottedClassPath) throws IOException {
        InputStream classStream = classpath.getResourceAsStream(asResourceName(dottedClassPath));
        analyse(checker, classStream);
    }

    private void analyseFromClassLoader(ClassVisitor checker, Dotted className) throws Exception {
        InputStream classStream = getClass().getClassLoader().getResourceAsStream(asResourceName(className));
        analyse(checker, classStream);
    }

    private void analyse(ClassVisitor checker, InputStream classStream) throws IOException {
        ClassReader cr = new ClassReader(classStream);
        cr.accept(checker, 0);
    }

    private String asResourceName(Dotted className) {
        return className.asString().replace(".", "/").concat(".class");
    }

    private AnalysisError attemptRecovery(ClassVisitor checker,
                                 Dotted className,
                                 Iterable<AnalysisResult> resultsSoFar,
                                 Throwable error) {

        if (!isRecoverable(error) || exceptionPolicy == FAIL_FAST) {
            throw unhandledExceptionBuilder.unhandledException(error, resultsSoFar, checker, className);
        } else {
            return handleException(checker, className);
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

    private AnalysisError handleException(ClassVisitor checker, Dotted onClass) {
        String errorDescription = createErrorDescription(onClass);
        return new AnalysisError(onClass, getNameOfChecker(checker), errorDescription);
    }

    public String createErrorDescription(Dotted dottedClass) {
        return format("It is likely that the class %s has dependencies outwith the given class path.", dottedClass.asString());
    }

    private String getNameOfChecker(ClassVisitor checker) {
        return checker.getClass().getSimpleName();
    }
}
