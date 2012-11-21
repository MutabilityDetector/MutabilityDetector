package de.htwg_konstanz.jia.testsubjects.variables;

import java.util.Date;

import org.junit.Test;

import de.htwg_konstanz.jia.testsubjects.AbstractMutabilityAsserter;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 21.11.2012
 */
public final class DirectReturnOfMutableTypeTest extends AbstractMutabilityAsserter {

    public DirectReturnOfMutableTypeTest() {
        super(DirectReturnOfMutableType.class);
    }

    @Test
    public void returnOfMutableTypeWithoutDefensiveCopyLeadsToMutability() {
        assertIsMutable();
    }

    @Test
    public void isMutableBecauseOfAppropriateReason() {
        assertNoReasonIsThat(fieldHasMutableType("date", Date.class));
    }

}
