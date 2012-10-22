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

import java.io.IOException;
import java.io.InputStream;

import org.mutabilitydetector.checkers.AsmMutabilityChecker;
import org.mutabilitydetector.locations.Dotted;
import org.objectweb.asm.ClassReader;

import com.google.classpath.ClassPath;
import com.google.classpath.ClassPathFactory;

public final class CheckerRunner {

    private ClassReader cr;
    private final ClassPath classpath;
    private final UnhandledExceptionBuilder unhandledExceptionBuilder;

    private CheckerRunner(ClassPath classpath, UnhandledExceptionBuilder unhandledExceptionBuilder) {
        this.classpath = classpath;
        this.unhandledExceptionBuilder = unhandledExceptionBuilder;
    }

    public static CheckerRunner createWithClasspath(ClassPath classpath) {
        return new CheckerRunner(classpath, new UnhandledExceptionBuilder());
    }

    public static CheckerRunner createWithCurrentClasspath() {
        return createWithClasspath(new ClassPathFactory().createFromJVM());
    }

    public void run(AnalysisSession analysisSession, AnalysisErrorReporter errorReporter, AsmMutabilityChecker checker, Dotted className) {
        try {
            try {
                cr = new ClassReader(className.asString());
                cr.accept(checker, 0);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                throw e;
            } catch (NoClassDefFoundError e) {
                analyseAsStream(checker, className.asString());
            } catch (IOException e) {
                analyseAsStream(checker, className.asString());
            }
        } catch (Throwable e) {
            handleException(errorReporter, checker, className.asString(), e);
            checker.visitAnalysisException(e);
            throw unhandledExceptionBuilder.unhandledException(e, analysisSession, checker, className);
        }
    }

    private void analyseAsStream(AsmMutabilityChecker checker, String dottedClassPath) throws IOException {
        String slashedClassPath = dottedClassPath.replace(".", "/").concat(".class");
        InputStream classStream = classpath.getResourceAsStream(slashedClassPath);
        cr = new ClassReader(classStream);
        cr.accept(checker, 0);
    }

    private void handleException(AnalysisErrorReporter analysisSession,
            AsmMutabilityChecker checker,
            String dottedClassPath,
            Throwable e) {
        String errorDescription = createErrorDescription(dottedClassPath);
        checker.visitAnalysisException(e);
        AnalysisErrorReporter.AnalysisError error = new AnalysisErrorReporter.AnalysisError(dottedClassPath, getNameOfChecker(checker), errorDescription);
        analysisSession.addAnalysisError(error);
    }

    public String createErrorDescription(String dottedClassPath) {
        return format("It is likely that the class %s has dependencies outwith the given class path.", dottedClassPath);
    }

    private String getNameOfChecker(AsmMutabilityChecker checker) {
        String checkerName = checker.getClass().getName();
        checkerName = checkerName.substring(checkerName.lastIndexOf(".") + 1);
        return checkerName;

    }
}
