package org.mutabilitydetector.findbugs;


import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.ba.AnalysisContext;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.ba.JCIPAnnotationDatabase;

public class ThisPluginDetector implements Detector {
    private static final String loggingLabel = MutabilityDetector.class.getSimpleName();
	
	static {
        System.out.printf("Registered plugin detector [%s]%n", loggingLabel);
    }

    private MutabilityDetector actualDetector;
	
	
	public ThisPluginDetector(BugReporter bugReporter) {
		this.actualDetector = new MutabilityDetector(this, bugReporter);
	}
	
    public void report() { }

	public void visitClassContext(ClassContext classContext) {
	    JCIPAnnotationDatabase jcipAnotationDatabase = AnalysisContext.currentAnalysisContext().getJCIPAnnotationDatabase();
	    if (jcipAnotationDatabase.hasClassAnnotation(classContext.getJavaClass().getClassName().replace('/', '.'), "Immutable")) {
	        actualDetector.visitClassContext(classContext);
	    }
	}

}
