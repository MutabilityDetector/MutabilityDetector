package org.mutabilitydetector.findbugs;


import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.CheckerRunnerFactory;
import org.mutabilitydetector.IAnalysisSession;
import org.mutabilitydetector.MutabilityCheckerFactory;
import org.mutabilitydetector.repackaged.com.google.classpath.ClassPath;

import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.classfile.Global;
import edu.umd.cs.findbugs.classfile.IClassPath;

public class ThisPluginDetector implements Detector {
    private static final String loggingLabel = MutabilityDetectorFindBugsPlugin.class.getSimpleName();
    
    static {
        System.out.printf("Registered plugin detector [%s]%n", loggingLabel);
    }
    
    private final BugReporter bugReporter;
    
    public ThisPluginDetector(BugReporter bugReporter) {
        this.bugReporter = bugReporter;
    }

    public void report() { }
    
    public void visitClassContext(ClassContext classContext) {
        new MutabilityDetectorFindBugsPlugin(this, bugReporter, new AnalysisSessionHolder()).visitClassContext(classContext);
    }
    
    public static class AnalysisSessionHolder {
        private volatile IAnalysisSession analysisSession = null;
        
        public IAnalysisSession lazyGet() {
            if (analysisSession == null) {
                analysisSession = createNewAnalysisSession();
            }
            
            return analysisSession;
        }

        private IAnalysisSession createNewAnalysisSession() {
            return makeFindBugsClasspathAvailable();
        }
        
        private IAnalysisSession makeFindBugsClasspathAvailable() {
            IClassPath findBugsClassPath = Global.getAnalysisCache().getClassPath();
            
            try {
                List<String> codeBasePaths = new FBCodeBasePathExtractor().listOfCodeBasePaths(findBugsClassPath);
                
                setCustomClassLoader(codeBasePaths);
                
                ClassPath mutabilityDetectorClasspath = new FBClasspathConverter().createClassPathForCodeBases(codeBasePaths);
                return AnalysisSession.createWithGivenClassPath(mutabilityDetectorClasspath, 
                        new CheckerRunnerFactory(mutabilityDetectorClasspath), 
                        new MutabilityCheckerFactory());
            } catch (InterruptedException e) {
                throw new ExceptionInInitializerError("Problem getting class path entries from FindBugs");
            }
        }
        
        private void setCustomClassLoader(List<String> codeBasePaths) {
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
    }

    
}
