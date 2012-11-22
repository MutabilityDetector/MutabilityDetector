/**
 * 
 */
package de.htwg_konstanz.jia.mdclient;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;

import javax.annotation.concurrent.Immutable;

import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.unittesting.internal.AnalysisSessionHolder;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 19.11.2012
 */
@Immutable
public final class MutabilityAsserter {

    private MutabilityAsserter() {
        throw new AssertionError();
    }
    
    public static AnalysisResultAsserter assertIsMutable(final Class<?> classToAnalyse) {
        final AnalysisResult analysisResult = AnalysisSessionHolder.analysisResultFor(classToAnalyse);
        final String message = format("Expected '%s' to be immutable.", classToAnalyse.getName());
        assertEquals(message, IsImmutable.NOT_IMMUTABLE, analysisResult.isImmutable);
        return new AnalysisResultAsserter(analysisResult);
    }

}
