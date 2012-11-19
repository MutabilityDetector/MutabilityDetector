/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.methods;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.Reason;
import org.mutabilitydetector.locations.ClassLocation;
import org.mutabilitydetector.unittesting.internal.AnalysisSessionHolder;

import de.htwg_konstanz.jia.testsubjects.MutabilityAsserter;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 19.11.2012
 */
public final class BeanLikeTest extends MutabilityAsserter {

    private final Class<BeanLike> classToAnalyse = BeanLike.class;

    @Test
    public void classWithSetterMethodIsMutable() {
        final AnalysisResult analysisResult = AnalysisSessionHolder.analysisResultFor(classToAnalyse);
        assertMutable(analysisResult);
        assertAppropriateReason(analysisResult);
    }

    private void assertAppropriateReason(final AnalysisResult analysisResult) {
        boolean assertionsRun = false;
        for (final MutableReasonDetail mutableReasonDetail : analysisResult.reasons) {
            if (isClassLocation(mutableReasonDetail)) {
                assertionsRun = true;
                assertEquals(createExpectedMutableReasonDetail(), mutableReasonDetail);
            }
        }
        assertTrue("Assertions for appropriate reason were not run.", assertionsRun);
    }

    private boolean isClassLocation(final MutableReasonDetail mutableReasonDetail) {
        return mutableReasonDetail.codeLocation() instanceof ClassLocation;
    }

    private MutableReasonDetail createExpectedMutableReasonDetail() {
        final String expectedMessage = format("Field [%s] can be reassigned within method [%s]", "rate", "setRate");
        final ClassLocation location = ClassLocation.fromInternalName(classToAnalyse.getName());
        final Reason reason = MutabilityReason.FIELD_CAN_BE_REASSIGNED;
        return MutableReasonDetail.newMutableReasonDetail(expectedMessage, location, reason);
    }

}
