package org.mutabilitydetector.unittesting.linkedsourcelocationspike;

import java.io.PrintStream;
import java.io.PrintWriter;

public class MutabilityAssertionCause extends RuntimeException {
    
    
    private MutabilityAssertionCause nextCause;

    public MutabilityAssertionCause(String message, StackTraceElement primarySourceLine, MutabilityAssertionCause nextCause) {
        super(message);
        this.nextCause = nextCause;
        setStackTrace(new StackTraceElement[] { primarySourceLine });
    }

    @Override
    public String toString() {
        return getMessage();
    }

    public void printReasons(PrintStream s) {
        s.print(getMessage() + ". Primary source code location [ at " + getStackTrace()[0] + "]");
        
        if (nextCause != null) {
            s.println();
            nextCause.printReasons(s);
        }
    }

    public void printReasons(PrintWriter s) {
        s.print(getMessage() + ". Primary source code location [ at " + getStackTrace()[0] + "]");
        
        if (nextCause != null) {
            s.println();
            nextCause.printReasons(s);
        }
    }
}