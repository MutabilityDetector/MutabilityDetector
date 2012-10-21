package org.mutabilitydetector.runtime;

import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.ThreadUnsafeAnalysisSession;
import org.mutabilitydetector.AnalysisSession.RequestedAnalysis;
import org.mutabilitydetector.locations.Dotted;

public final class RuntimeAnalysisExample {
    private final AnalysisSession analysisSession = ThreadUnsafeAnalysisSession.createWithCurrentClassPath();

    private void run(Object requiredToBeImmutable) {
        Dotted runtimeClassOfGivenObject = Dotted.fromClass(requiredToBeImmutable.getClass());
		RequestedAnalysis requestedAnalysis = analysisSession.resultFor(runtimeClassOfGivenObject);
        AnalysisResult result = requestedAnalysis.result;
        
        if(result.isImmutable.equals(IsImmutable.IMMUTABLE)) {
            // rest safe in the knowledge the class is 
        	// immutable, share across threads with joyful abandon
        } else if(result.isImmutable.equals(IsImmutable.NOT_IMMUTABLE)) {
            // be careful here: make defensive copies, 
        	// don't publish the reference, 
        	// read Java Concurrency In Practice right away!
        }
    }
    
    public static void main(String[] args) {
        new RuntimeAnalysisExample().run(makeParam());
    }
    
    private static Object makeParam() {
        return new java.util.Date();
    }

}
