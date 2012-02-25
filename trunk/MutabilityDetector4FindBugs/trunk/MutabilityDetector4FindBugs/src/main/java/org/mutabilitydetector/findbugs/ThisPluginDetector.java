package org.mutabilitydetector.findbugs;


import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.CheckerRunnerFactory;
import org.mutabilitydetector.IAnalysisSession;
import org.mutabilitydetector.MutabilityCheckerFactory;
import org.mutabilitydetector.cli.RunMutabilityDetector;
import org.mutabilitydetector.repackaged.com.google.classpath.ClassPath;
import org.mutabilitydetector.repackaged.com.google.classpath.ClassPathFactory;

import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.bcel.AnnotationDetector;
import edu.umd.cs.findbugs.classfile.Global;
import edu.umd.cs.findbugs.classfile.IClassPath;
import edu.umd.cs.findbugs.classfile.ICodeBase;

public class ThisPluginDetector extends AnnotationDetector implements Detector {
    private static final String loggingLabel = MutabilityDetector.class.getSimpleName();
	
	static {
        System.out.printf("Registered plugin detector [%s]%n", loggingLabel);
    }
	
	/**
	 * Unfortunately this stuff is cribbed from {@link RunMutabilityDetector} internals.
	 */
	private static class AnalysisSessionHolder {
	    public static final IAnalysisSession analysisSession = initialise();
        
	    private static IAnalysisSession initialise() {
	        return makeFindBugsClasspathAvailable();
        }
	    
	    private static IAnalysisSession makeFindBugsClasspathAvailable() {
	        IClassPath findBugsClassPath = Global.getAnalysisCache().getClassPath();
	        
	        try {
	            List<String> codeBasePaths = listOfCodeBasePaths(findBugsClassPath);
	            ClassPath mutabilityDetectorClasspath = createClassPathForCodeBases(codeBasePaths);
	            setCustomClassLoader(codeBasePaths);
	            
	            return AnalysisSession.createWithGivenClassPath(mutabilityDetectorClasspath, 
	                                                            new CheckerRunnerFactory(mutabilityDetectorClasspath), 
	                                                            new MutabilityCheckerFactory());
	        } catch (InterruptedException e) {
	            throw new ExceptionInInitializerError("Problem getting class path entries from FindBugs");
	        }
	    }

	    private static void setCustomClassLoader(List<String> codeBasePaths) {
	        List<URL> urlList = new ArrayList<URL>();

	        for (String classPathUrl : codeBasePaths) {
	            try {
	                URL toAdd = new File(classPathUrl).toURI().toURL();
	                urlList.add(toAdd);
	            } catch (MalformedURLException e) {
	                System.err.printf("Classpath option %s is invalid.", classPathUrl);
	            }
	        }
	        
	        ClassLoader classLoader = new URLClassLoader(urlList.toArray(new URL[] {}), 
	                                                     Thread.currentThread().getContextClassLoader());
	        
	        Thread.currentThread().setContextClassLoader(classLoader);
	    }

        private static ClassPath createClassPathForCodeBases(List<String> codeBasePaths) {
            StringBuilder allClassPathsInString = new StringBuilder();
	        for (String classPathUrl : codeBasePaths) {
                allClassPathsInString.append(classPathUrl + File.pathSeparator);
            }
	        
	        return new ClassPathFactory().createFromPath(allClassPathsInString.toString());
        }

        private static List<String> listOfCodeBasePaths(IClassPath findBugsClassPath) throws InterruptedException {
            List<String> codeBasePaths = new ArrayList<String>();
	        pathsFromCodeBase(codeBasePaths, findBugsClassPath.appCodeBaseIterator());
	        pathsFromCodeBase(codeBasePaths, findBugsClassPath.auxCodeBaseIterator());
            return codeBasePaths;
        }

        private static void pathsFromCodeBase(List<String> codeBasePaths, Iterator<? extends ICodeBase> codeBaseIterator) {
            while (codeBaseIterator.hasNext()) {
	            ICodeBase codeBase = codeBaseIterator.next();
	            String pathName = codeBase.getPathName();
	            
	            if (pathName != null) {
	                codeBasePaths.add(pathName);
	            }
	        }
        }
	    
	}

    private boolean doMutabilityDetectionOnCurrentClass;
    private final BugReporter bugReporter;
	
	public ThisPluginDetector(BugReporter bugReporter) {
		this.bugReporter = bugReporter;
	}
	
    public void report() { }

    
    
	public void visitClassContext(ClassContext classContext) {
	    doMutabilityDetectionOnCurrentClass = false;
	    
	    super.visitClassContext(classContext);
	    
	    if (doMutabilityDetectionOnCurrentClass) {
	        new MutabilityDetector(this, bugReporter, AnalysisSessionHolder.analysisSession)
	            .visitClassContext(classContext);
	    }
	}
	
	@Override
	public void visitAnnotation(String annotationClass, Map<String, Object> map, boolean runtimeVisible) {
	    super.visitAnnotation(annotationClass, map, runtimeVisible);
	    
        doMutabilityDetectionOnCurrentClass = annotationClass.endsWith(".Immutable");
	}

}
