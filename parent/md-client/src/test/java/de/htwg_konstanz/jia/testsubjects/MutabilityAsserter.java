/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects;

import static org.junit.Assert.assertEquals;

import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.unittesting.internal.AnalysisSessionHolder;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 19.11.2012
 */
public abstract class MutabilityAsserter {

    public void assertMutable(final Class<?> classToAnalyse) {
        defaultAssertMutable(AnalysisSessionHolder.analysisResultFor(classToAnalyse));
    }

    public void assertMutable(final AnalysisResult analysisResult) {
        defaultAssertMutable(analysisResult);
    }

    protected final void defaultAssertMutable(final AnalysisResult analysisResult) {
        assertEquals(IsImmutable.NOT_IMMUTABLE, analysisResult.isImmutable);
    }

}
