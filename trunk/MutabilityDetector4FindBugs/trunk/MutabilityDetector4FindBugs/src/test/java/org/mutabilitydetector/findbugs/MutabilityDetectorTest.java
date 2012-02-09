package org.mutabilitydetector.findbugs;

import static com.youdevise.fbplugins.tdd4fb.DetectorAssert.assertBugReported;
import static com.youdevise.fbplugins.tdd4fb.DetectorAssert.assertNoBugsReported;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;

import com.youdevise.fbplugins.tdd4fb.DetectorAssert;

import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.ba.AnalysisContext;
import edu.umd.cs.findbugs.detect.NoteJCIPAnnotation;

public class MutabilityDetectorTest {

    @Test
    public void doesNotRaiseBugForImmutableClass() throws Exception {
        BugReporter bugReporter = DetectorAssert.bugReporterForTesting();
        Detector detector = new ThisPluginDetector(bugReporter);
        setupAnalysisSessionToHaveScannedForImmutableAnnotation(ImmutableExample.class);        
        
        assertNoBugsReported(ImmutableExample.class, detector, bugReporter);
    }

    @Test
    public void doesNotRaiseBugForMutableClassNotAnnotatedWithImmutable() throws Exception {
        BugReporter bugReporter = DetectorAssert.bugReporterForTesting();
        Detector detector = new ThisPluginDetector(bugReporter);
        setupAnalysisSessionToHaveScannedForImmutableAnnotation(MutableButNotAnnotated.class);        
        
        assertNoBugsReported(MutableButNotAnnotated.class, detector, bugReporter);
    }

    @Test
    public void raisesBugForMutableClassAnnotatedWithImmutable() throws Exception {
        BugReporter bugReporter = DetectorAssert.bugReporterForTesting();
        Detector detector = new ThisPluginDetector(bugReporter);
        setupAnalysisSessionToHaveScannedForImmutableAnnotation(MutableAndAnnotated.class);        
        
        assertBugReported(MutableAndAnnotated.class, detector, bugReporter);
    }
    
    
    private void setupAnalysisSessionToHaveScannedForImmutableAnnotation(Class<?> lookForAnnotation)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        
        runAnArbritaryDetectorToInitialiseFindBugs(lookForAnnotation);
        
        AnalysisContext.currentAnalysisContext()
            .getJCIPAnnotationDatabase()
            .getEntryForClass(lookForAnnotation.getClass().getName())
            .put("Immutable", null);
    }

    private void runAnArbritaryDetectorToInitialiseFindBugs(Class<?> lookForAnnotation) throws ClassNotFoundException,
            NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class<?> detectorRunnerClass = Class.forName("com.youdevise.fbplugins.tdd4fb.DetectorRunner");
        Method runDetector = detectorRunnerClass.getMethod("runDetectorOnClass", Detector.class, Class.class, BugReporter.class);
        runDetector.setAccessible(true);
        BugReporter reporterForTesting = DetectorAssert.bugReporterForTesting();
        runDetector.invoke(null, new NoteJCIPAnnotation(reporterForTesting), lookForAnnotation, reporterForTesting);
    }
}
