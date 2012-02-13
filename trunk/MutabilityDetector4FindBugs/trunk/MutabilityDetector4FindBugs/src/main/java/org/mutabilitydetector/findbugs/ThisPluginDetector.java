package org.mutabilitydetector.findbugs;


import java.util.Map;

import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.IAnalysisSession;

import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.bcel.AnnotationDetector;

public class ThisPluginDetector extends AnnotationDetector implements Detector {
    private static final String loggingLabel = MutabilityDetector.class.getSimpleName();
	
	static {
        System.out.printf("Registered plugin detector [%s]%n", loggingLabel);
    }
	
	private static class AnalysisSessionHolder {
	    public static final IAnalysisSession analysisSession = initialise();
        
	    private static IAnalysisSession initialise() {
            return AnalysisSession.createWithCurrentClassPath();
        }
	}

    private final MutabilityDetector actualDetector;
    private boolean doMutabilityDetectionOnCurrentClass;
	
	public ThisPluginDetector(BugReporter bugReporter) {
		this.actualDetector = new MutabilityDetector(this, bugReporter, AnalysisSessionHolder.analysisSession);
	}
	
    public void report() { }

    
    
	public void visitClassContext(ClassContext classContext) {
	    doMutabilityDetectionOnCurrentClass = false;
	    
	    super.visitClassContext(classContext);

	    if (doMutabilityDetectionOnCurrentClass) {
	        actualDetector.visitClassContext(classContext);
	    }
	}
	
	@Override
	public void visitAnnotation(String annotationClass, Map<String, Object> map, boolean runtimeVisible) {
	    super.visitAnnotation(annotationClass, map, runtimeVisible);
	    
        doMutabilityDetectionOnCurrentClass = annotationClass.endsWith(".Immutable");
	}

}
