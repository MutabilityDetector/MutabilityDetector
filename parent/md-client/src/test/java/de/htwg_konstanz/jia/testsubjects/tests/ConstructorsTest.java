/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.tests;

import static de.htwg_konstanz.jia.mdclient.MutabilityAsserter.assertIsMutable;
import static de.htwg_konstanz.jia.mdclient.matcher.Matcher.fieldHasMutableType;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;

import java.util.Date;

import org.junit.Test;

import de.htwg_konstanz.jia.testsubjects.constructors.DirectAssignmentOfMutableType;
import de.htwg_konstanz.jia.testsubjects.constructors.DirectAssignmentOfPrimitiveType;
import de.htwg_konstanz.jia.testsubjects.constructors.IndirectAssignmentOfMutableType;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 27.11.2012
 */
public final class ConstructorsTest {

    @Test
    public void rendersMutableForDirectAssignmentOfMutableType() {
        assertIsMutable(DirectAssignmentOfMutableType.class).andNoReasonIsThat(fieldHasMutableType("date", Date.class));
    }

    @Test
    public void doesNotRenderMutableForDirectlyAssigningPrimitiveType() {
        assertInstancesOf(DirectAssignmentOfPrimitiveType.class, areImmutable());
    }

    @Test
    public void doesNotRenderMutableForAssigningDefensiveCopyOfMutableType() {
        assertInstancesOf(IndirectAssignmentOfMutableType.class, areImmutable());
    }

}
