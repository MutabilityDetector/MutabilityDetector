package org.mutabilitydetector.findbugs;

import static com.youdevise.fbplugins.tdd4fb.DetectorAssert.assertBugReported;
import static com.youdevise.fbplugins.tdd4fb.DetectorAssert.assertNoBugsReported;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.mutabilitydetector.findbugs.warningtypes.AbstractInherentlyMutable;
import org.mutabilitydetector.findbugs.warningtypes.AssignAbstractTypeToField;
import org.mutabilitydetector.findbugs.warningtypes.CanBeSubclassed;
import org.mutabilitydetector.findbugs.warningtypes.EscapedThisReference;
import org.mutabilitydetector.findbugs.warningtypes.MutableTypeToField;
import org.mutabilitydetector.findbugs.warningtypes.NonFinalField;
import org.mutabilitydetector.findbugs.warningtypes.PublishedNonFinalField;
import org.mutabilitydetector.findbugs.warningtypes.ReassignField;
import org.mutabilitydetector.findbugs.warningtypes.UseArrayField;
import org.mutabilitydetector.locations.ClassName;

import com.youdevise.fbplugins.tdd4fb.DetectorAssert;

import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.ba.AnalysisContext;
import edu.umd.cs.findbugs.detect.NoteJCIPAnnotation;

@RunWith(Theories.class)
public class MutabilityDetectorTest {

    @Before public void setUp() throws Exception {
        bugReporter = DetectorAssert.bugReporterForTesting();
        detector = new ThisPluginDetector(bugReporter);
        
        setupAnalysisSessionToHaveScannedForImmutableAnnotation(ImmutableExample.class);        
    }
    
    @Test
    public void doesNotRaiseBugForImmutableClass() throws Exception {
        assertNoBugsReported(ImmutableExample.class, detector, bugReporter);
    }

    @Test
    public void doesNotRaiseBug() throws Exception {
        assertNoBugsReported(ClassName.class, detector, bugReporter);
    }

    @Test
    public void doesNotRaiseBugForMutableClassNotAnnotatedWithImmutable() throws Exception {
        assertNoBugsReported(MutableButNotAnnotated.class, detector, bugReporter);
    }

    @Test
    public void raisesBugForMutableClassAnnotatedWithImmutable() throws Exception {
        assertBugReported(MutableAndAnnotated.class, detector, bugReporter);
    }
    
    
    @DataPoints public static BugDataPoint[] expectedBugs = new BugDataPoint[] {
        new BugDataPoint(AbstractInherentlyMutable.class, "MUTDEC_ABSTRACT_TYPE_INHERENTLY_MUTABLE"),
        new BugDataPoint(AssignAbstractTypeToField.class, "MUTDEC_ABSTRACT_TYPE_TO_FIELD"),
        new BugDataPoint(CanBeSubclassed.class, "MUTDEC_CAN_BE_SUBCLASSED"),
        new BugDataPoint(EscapedThisReference.class, "MUTDEC_ESCAPED_THIS_REFERENCE"),
        new BugDataPoint(MutableTypeToField.class, "MUTDEC_MUTABLE_TYPE_TO_FIELD"),
        new BugDataPoint(NonFinalField.class, "MUTDEC_NON_FINAL_FIELD"),
        new BugDataPoint(PublishedNonFinalField.class, "MUTDEC_PUBLISHED_NON_FINAL_FIELD"),
        new BugDataPoint(ReassignField.class, "MUTDEC_FIELD_CAN_BE_REASSIGNED"),
        new BugDataPoint(UseArrayField.class, "MUTDEC_ARRAY_TYPE_INHERENTLY_MUTABLE"),
    };
    
    private BugReporter bugReporter;
    private Detector detector;
    
    private static class BugDataPoint {
        public final Class<?> toAnalyse;
        public final String bugType;

        public BugDataPoint(Class<?> toAnalyse, String expectedBugType) {
            this.toAnalyse = toAnalyse;
            this.bugType = expectedBugType;
        }
    }
    
    @Theory
    public void correctWarningTypeIsRegistered(BugDataPoint expected) throws Exception {
        bugReporter = DetectorAssert.bugReporterForTesting();
        detector = new ThisPluginDetector(bugReporter);
        
        setupAnalysisSessionToHaveScannedForImmutableAnnotation(ImmutableExample.class);       
        assertBugReported(expected.toAnalyse, detector, bugReporter, DetectorAssert.ofType(expected.bugType));
    }
    
    
    private void setupAnalysisSessionToHaveScannedForImmutableAnnotation(Class<?> lookForAnnotation) throws Exception {
        runAnArbritaryDetectorToInitialiseFindBugs(lookForAnnotation);
        
        AnalysisContext.currentAnalysisContext()
            .getJCIPAnnotationDatabase()
            .getEntryForClass(lookForAnnotation.getClass().getName())
            .put("Immutable", null);
    }

    private void runAnArbritaryDetectorToInitialiseFindBugs(Class<?> lookForAnnotation) throws Exception {
        Class<?> detectorRunnerClass = Class.forName("com.youdevise.fbplugins.tdd4fb.DetectorRunner");
        Method runDetector = detectorRunnerClass.getMethod("runDetectorOnClass", Detector.class, Class.class, BugReporter.class);
        runDetector.setAccessible(true);
        BugReporter reporterForTesting = DetectorAssert.bugReporterForTesting();
        runDetector.invoke(null, new NoteJCIPAnnotation(reporterForTesting), lookForAnnotation, reporterForTesting);
    }
}
