/**
 * 
 */
package de.htwg_konstanz.jia.mdclient;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.unittesting.internal.AnalysisSessionHolder;

/**
 * Utility class to assert mutability of a particular class.
 * 
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 19.11.2012
 */
@Immutable
public final class MutabilityAsserter {

    /*
     * Inhibit instantiation.
     */
    private MutabilityAsserter() {
        throw new AssertionError();
    }

    /**
     * Asserts that the given class is mutable.
     * 
     * @param classToAnalyse
     *            the class to analyse. Must not be {@code null}.
     * @return an instance of {@link AnalysisResultAsserter} to process the
     *         gained analysis result.
     */
    public static AnalysisResultAsserter assertIsMutable(final Class<?> classToAnalyse) {
        Validate.notNull(classToAnalyse);

        final AnalysisResult analysisResult = AnalysisSessionHolder.analysisResultFor(classToAnalyse);
        final String message = format("Expected '%s' to be immutable.", classToAnalyse.getName());
        assertEquals(message, IsImmutable.NOT_IMMUTABLE, analysisResult.isImmutable);

        return DefaultAnalysisResultAsserter.getInstance(analysisResult);
    }

    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this);
        return builder.toString();
    }

}
