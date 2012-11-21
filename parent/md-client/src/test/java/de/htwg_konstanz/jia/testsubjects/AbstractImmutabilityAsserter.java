/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects;

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
public abstract class AbstractImmutabilityAsserter implements ReasonPrinter {

    public void assertIsImmutable(final Class<?> classToAnalyse) {
        defaultAssertIsImmutable(classToAnalyse);
    }

    protected final void defaultAssertIsImmutable(final Class<?> classToAnalyse) {
        final AnalysisResult analysisResult = AnalysisSessionHolder.analysisResultFor(classToAnalyse);
        final Collection<MutableReasonDetail> reasons = analysisResult.reasons;
        printReasonsIfNotEmpty(classToAnalyse.getName(), reasons);
        assertIsImmutable(analysisResult.isImmutable);
    }

    private void assertIsImmutable(final IsImmutable isImmutable) {
        assertEquals(IsImmutable.IMMUTABLE, isImmutable);
    }

    @Override
    public void printReasonsIfNotEmpty(final String className, final Collection<MutableReasonDetail> reasons) {
        final ReasonPrinter reasonPrinter = DefaultReasonPrinter.getInstance();
        reasonPrinter.printReasonsIfNotEmpty(className, reasons);
    }

}
