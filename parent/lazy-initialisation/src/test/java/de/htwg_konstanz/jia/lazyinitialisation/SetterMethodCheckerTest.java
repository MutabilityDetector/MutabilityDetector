package de.htwg_konstanz.jia.lazyinitialisation;

import static org.mutabilitydetector.ThreadUnsafeAnalysisSession.createWithCurrentClassPath;
import static org.mutabilitydetector.checkers.SetterMethodChecker.newSetterMethodChecker;
import static org.mutabilitydetector.CheckerRunner.ExceptionPolicy.FAIL_FAST;
import static org.mutabilitydetector.TestUtil.testingVerifierFactory;

import java.io.IOException;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.CheckerRunner;
import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.checkers.SetterMethodChecker;
import org.mutabilitydetector.checkers.info.PrivateMethodInvocationInformation;
import org.mutabilitydetector.checkers.info.SessionCheckerRunner;
import org.mutabilitydetector.locations.ClassName;
import org.mutabilitydetector.locations.Dotted;
import org.objectweb.asm.ClassReader;

public final class SetterMethodCheckerTest {

    private SetterMethodChecker checker;
    private CheckerRunner checkerRunner;
    private AnalysisSession analysisSession;
    private PrivateMethodInvocationInformation info;


    @Before
    public void setUp() {
        checkerRunner = CheckerRunner.createWithCurrentClasspath(FAIL_FAST);
        analysisSession = createWithCurrentClassPath();
        info = new PrivateMethodInvocationInformation(new SessionCheckerRunner(analysisSession, checkerRunner));
        checker = newSetterMethodChecker(info, testingVerifierFactory());
    }

    @Test
    public void verifyJavaLangString() throws IOException {
        final ClassName dotted = Dotted.fromClass(String.class);
        final ClassReader cr = new ClassReader(dotted.asString());
        cr.accept(checker, 0);
        final IsImmutable result = checker.result();
        final Collection<MutableReasonDetail> reasons = checker.reasons();
        System.out.println(result);
    }

}
