package org.mutabilitydetector.unittesting.linkedsourcelocationspike;

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