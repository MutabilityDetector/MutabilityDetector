/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.constructors;

import java.util.Date;

import org.junit.Test;

import de.htwg_konstanz.jia.testsubjects.AbstractMutabilityAsserter;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 19.11.2012
 */
public final class DirectAssignmentOfMutableTypeTest extends AbstractMutabilityAsserter {

    public DirectAssignmentOfMutableTypeTest() {
        super(DirectAssignmentOfMutableType.class);
    }

    @Test
    public void directAssignmentOfMutableTypeLeadsToMutableClass() {
        assertIsMutable();
    }

    @Test
    public void reasonForMutabilityIsNotUsageOfMutableType() {
        assertNoReasonIsThat(fieldHasMutableType("date", Date.class));
    }

}
