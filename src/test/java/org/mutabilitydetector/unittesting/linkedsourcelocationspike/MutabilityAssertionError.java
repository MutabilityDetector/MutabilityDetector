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

package org.mutabilitydetector.unittesting.linkedsourcelocationspike;

import java.io.PrintStream;
import java.io.PrintWriter;



public class MutabilityAssertionError extends AssertionError {

    private final MutabilityAssertionCause chainedCauses;

    public MutabilityAssertionError(String message) {
        super(message);
        chainedCauses = null;
    }
    
    public MutabilityAssertionError(String message, MutabilityAssertionCause chainedCauses) {
        super(message);
        this.chainedCauses = chainedCauses;
        initCause(chainedCauses);
    }
    
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
    
    public void printStackTrace(PrintStream s) {
        synchronized (s) {
            s.println(this);

            chainedCauses.printReasons(s);
        }
    }

    @Override
    public String toString() {
        return getMessage();
    }


    @Override
    public void printStackTrace() {
        printStackTrace(System.err);
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        synchronized (s) {
            s.println(this);

            chainedCauses.printReasons(s);
        }
    }
}
