package org.mutabilitydetector.findbugs;

import static com.youdevise.fbplugins.tdd4fb.DetectorAssert.assertNoBugsReported;

import org.junit.Test;

import com.youdevise.fbplugins.tdd4fb.DetectorAssert;

import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;

public class MutabilityDetectorTest {

    @Test
    public void doesNotRaiseBugForImmutableClass() throws Exception {
        BugReporter bugReporter = DetectorAssert.bugReporterForTesting();
        Detector detector = new ThisPluginDetector(bugReporter);
        
        assertNoBugsReported(ImmutableExample.class, detector, bugReporter);
    }
    
}
