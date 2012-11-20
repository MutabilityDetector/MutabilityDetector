/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.constructors;

import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.locations.ClassLocation;

import de.htwg_konstanz.jia.testsubjects.MutabilityAsserter;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 19.11.2012
 */
public final class DirectAssignmentOfMutableTypeTest extends MutabilityAsserter {

    public DirectAssignmentOfMutableTypeTest() {
        super(DirectAssignmentOfMutableType.class);
    }

    @Test
    public void directAssignmentOfMutableTypeLeadsToMutableClass() {
        assertIsMutable();
        assertAppropriateReason();
    }

    private void assertAppropriateReason() {
        final Collection<MutableReasonDetail> reasons = analysisResult.reasons;
        boolean assertionsRun = false;
        for (final MutableReasonDetail mutableReasonDetail : reasons) {
            if (isClassLocation(mutableReasonDetail)) {
                assertionsRun = true;
            }
        }
        assertTrue("Assertions for appropriate reason were not run.", assertionsRun);
    }

    private boolean isClassLocation(final MutableReasonDetail mutableReasonDetail) {
        return mutableReasonDetail.codeLocation() instanceof ClassLocation;
    }

}
