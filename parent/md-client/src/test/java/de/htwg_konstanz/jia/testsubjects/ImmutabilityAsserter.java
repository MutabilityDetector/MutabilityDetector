/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.unittesting.internal.AnalysisSessionHolder;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 20.11.2012
 */
public abstract class ImmutabilityAsserter {

    public void assertImmutable(final Class<?> classToAnalyse) {
        defaultAssertImmutable(classToAnalyse);
    }

    protected final void defaultAssertImmutable(final Class<?> classToAnalyse) {
        final AnalysisResult analysisResult = AnalysisSessionHolder.analysisResultFor(classToAnalyse);
        final Collection<MutableReasonDetail> reasons = analysisResult.reasons;
        printReasonsIfNotEmpty(classToAnalyse.getName(), reasons);
        assertImmutable(analysisResult.isImmutable);
    }

    private void assertImmutable(final IsImmutable isImmutable) {
        assertEquals(IsImmutable.IMMUTABLE, isImmutable);
    }

    private void printReasonsIfNotEmpty(final String className, final Collection<MutableReasonDetail> reasons) {
        if (!reasons.isEmpty()) {
            final String classMessage = format("Analysed class: '%s' is mutable because of", className);
            System.out.println(classMessage);
            for (final MutableReasonDetail mutableReasonDetail : reasons) {
                final String reasonMessage = format("    * reason: %s", mutableReasonDetail.reason());
                System.out.println(reasonMessage);
            }
        }
    }

}
