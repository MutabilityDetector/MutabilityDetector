/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.transitivity;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.Reason;
import org.mutabilitydetector.locations.ClassLocation;
import org.mutabilitydetector.locations.CodeLocation;
import org.mutabilitydetector.locations.Dotted;
import org.mutabilitydetector.locations.FieldLocation;
import org.mutabilitydetector.unittesting.internal.AnalysisSessionHolder;

import de.htwg_konstanz.jia.testsubjects.MutabilityAsserter;
import de.htwg_konstanz.jia.testsubjects.transitivity.TransitiveMutable.NestedMutable;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 19.11.2012
 */
public final class TransitiveMutableTest extends MutabilityAsserter {

    private final Class<TransitiveMutable> classToAnalyse = TransitiveMutable.class;

    @Test
    public void mutabilityIsTransitive() {
        final AnalysisResult analysisResult = AnalysisSessionHolder.analysisResultFor(classToAnalyse);
        assertMutable(analysisResult);
        assertAppropriateReason(analysisResult);
    }

    private void assertAppropriateReason(final AnalysisResult analysisResult) {
        final Collection<MutableReasonDetail> reasons = analysisResult.reasons;
        for (final MutableReasonDetail mutableReasonDetail : reasons) {

            // There should be exactly one - the expected - reason.
            final MutableReasonDetail expectedMutableReasonDetail = createExpectedMutableReasonDetail();
            assertEquals(expectedMutableReasonDetail, mutableReasonDetail);
            break;
        }
    }

    private MutableReasonDetail createExpectedMutableReasonDetail() {
        final Reason reason = MutabilityReason.MUTABLE_TYPE_TO_FIELD;
        return MutableReasonDetail.newMutableReasonDetail(createExpectedMessage(), createFieldLocation(), reason);
    }

    private String createExpectedMessage() {
        final String typeName = Dotted.fromClass(NestedMutable.class).toString();
        return format("Field can have a mutable type (%s) assigned to it.", typeName);
    }

    private CodeLocation<FieldLocation> createFieldLocation() {
        final CodeLocation<ClassLocation> classLocation = ClassLocation.fromInternalName(classToAnalyse.getName());
        return FieldLocation.fieldLocation("nested", (ClassLocation) classLocation);
    }

}
