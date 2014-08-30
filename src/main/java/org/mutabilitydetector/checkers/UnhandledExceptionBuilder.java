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

import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.locations.Dotted;

public final class UnhandledExceptionBuilder {

    private static final String UNHANDLED_ERROR_MESSAGE = String.format(
        "%nAn unhandled error occurred. This is probably my fault, not yours, and I am sorry.%n" +
        "I'd love to get an opportunity to fix this, please report as an issue at:%n " +
        "https://github.com/MutabilityDetector/MutabilityDetector/issues/ %n" +
        "Pasting in this error message and stack trace, and if possible, %n" +
        "information about the code causing the error. %n" +
        "For example, one of: %n" +
        "    .class files (preferably with source);%n" +
        "    compilable .java files; %n" +
        "    a jar (again preferably with source);%n" +
        "    or, if your project is open source, information on where I can get the code from%n" +
        "        (I'm happy to checkout and build your project in order to investigate the error).%n%n" +
        "Apologies, and thank you for using Mutability Detector.%n%n");
    
    public MutabilityAnalysisException unhandledException(Throwable cause, 
                                                           Iterable<AnalysisResult> resultsSoFar,
                                                           AsmMutabilityChecker checker, 
                                                           Dotted className) {
        
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append(format("%nAn unhandled error occurred. Please read message at end of this output.%n%n"));
        errorMessage.append(format("Class being analysed: %s%n", className.asString()));
        errorMessage.append(format("Checker that failed: %s%n", checker.getClass().getSimpleName()));
        errorMessage.append(format("Classes analysed so far:%n"));
        appendClassesAnalysed(errorMessage, resultsSoFar);
        
        errorMessage.append(UNHANDLED_ERROR_MESSAGE);
        
        return new MutabilityAnalysisException(errorMessage.toString(), cause);
    }

    private void appendClassesAnalysed(StringBuilder errorMessage, Iterable<AnalysisResult> resultsSoFar) {
        for (AnalysisResult result : resultsSoFar) {
            errorMessage.append(format("    %s%n", result.dottedClassName));
        }
    }

}
