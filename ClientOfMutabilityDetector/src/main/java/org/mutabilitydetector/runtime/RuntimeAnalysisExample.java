package org.mutabilitydetector.runtime;

import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.ThreadUnsafeAnalysisSession;
import org.mutabilitydetector.locations.Dotted;

public final class RuntimeAnalysisExample {
    /*
     * Create an instance of AnalysisSession. This is the object
     * that can tell you if a class is immutable or not.
     * 
     * Note there is only currently a thread unsafe implementation, 
     * however, it's intended there is no static state. So there 
     * should be no problem in creating one per thread. Since
     * analysisSessions cache their result, this will result in a 
     * cache miss per thread. This should amortize over time.
     */
    private final AnalysisSession analysisSession = ThreadUnsafeAnalysisSession.createWithCurrentClassPath();

    private void run(Object requiredToBeImmutable) {
        
        /* 
         * Even though the parameter is defined to be java.lang.Object 
         * we are interested in what the actual class is, at runtime.
         */
        Class<?> actualClassOfParameterAtRuntime = requiredToBeImmutable.getClass();
        
        /*
         * Convert class object into its a representation of its
         * dotted class name. 
         */
        Dotted dottedClassName = Dotted.fromClass(actualClassOfParameterAtRuntime);
        
        /*
         * Request an analysis of the runtime class, to discover if this
         * instance will be immutable or not. 
         */
        AnalysisResult result = analysisSession.resultFor(dottedClassName);

        if (result.isImmutable.equals(IsImmutable.IMMUTABLE)) {
            /*
             * rest safe in the knowledge the class is
             * immutable, share across threads with joyful abandon
             */
        } else if (result.isImmutable.equals(IsImmutable.NOT_IMMUTABLE)) {
            /*
             * be careful here: make defensive copies,
             * don't publish the reference,
             * read Java Concurrency In Practice right away!
             */
        }
    }

    public static void main(String[] args) {
        new RuntimeAnalysisExample().run(makeParam());
    }

    private static Object makeParam() {
        return new java.util.Date();
    }

}
