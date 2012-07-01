package org.mutabilitydetector;

import static java.lang.String.format;

import org.mutabilitydetector.checkers.AsmMutabilityChecker;
import org.mutabilitydetector.checkers.MutabilityAnalysisException;
import org.mutabilitydetector.locations.Dotted;

public final class UnhandledExceptionBuilder {

    private static final String UNHANDLED_ERROR_MESSAGE = String.format(
        "%nAn unhandled error occurred. This is probably my fault, not yours, and I am sorry.%n" +
        "I'd love to get an opportunity to fix this, please report as an issue at:%n " +
        "http://code.google.com/p/mutability-detector/issues/list %n" +
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
                                                           AnalysisSession analysisSession,
                                                           AsmMutabilityChecker checker, 
                                                           Dotted className) {
        
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append(format("%nAn unhandled error occurred. Please read message at end of this output.%n%n"));
        errorMessage.append(format("Class being analysed: %s%n", className.asString()));
        errorMessage.append(format("Checker that failed: %s%n", checker.getClass().getSimpleName()));
        errorMessage.append(format("Classes analysed so far:%n"));
        appendClassesAnalysed(errorMessage, analysisSession);
        
        errorMessage.append(UNHANDLED_ERROR_MESSAGE);
        
        return new MutabilityAnalysisException(errorMessage.toString(), cause);
    }

    private void appendClassesAnalysed(StringBuilder errorMessage, AnalysisSession analysisSession) {
        analysisSession.getResults().forEach(result -> { 
        	errorMessage.append(format("    %s%n", result.dottedClassName)); 
    	});
    }

}
