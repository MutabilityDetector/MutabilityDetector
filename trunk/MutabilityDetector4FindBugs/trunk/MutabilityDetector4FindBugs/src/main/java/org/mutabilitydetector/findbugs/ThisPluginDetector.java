package org.mutabilitydetector.findbugs;


import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.ba.ClassContext;

public class ThisPluginDetector implements Detector {
    private static final String loggingLabel = MutabilityDetector.class.getSimpleName();
	
    private static class SetupChecker {
        
        private SetupChecker() { }
        public static SetupChecker SINGLETON_INSTANCE = new SetupChecker();
    }

	static {
        System.out.printf("Registered plugin detector [%s]%n", loggingLabel);
    }

    private MutabilityDetector actualDetector;
	
	
	public ThisPluginDetector(BugReporter bugReporter) {
		this.actualDetector = new MutabilityDetector(this, bugReporter);
	}
	
    public void report() { }

	public void visitClassContext(ClassContext classContext) {
	    actualDetector.visitClassContext(classContext);
	}
	

}
