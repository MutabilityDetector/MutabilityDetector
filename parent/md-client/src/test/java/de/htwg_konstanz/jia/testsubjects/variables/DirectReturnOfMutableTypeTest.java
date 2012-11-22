package de.htwg_konstanz.jia.testsubjects.variables;

import static de.htwg_konstanz.jia.mdclient.AnalysisResultAsserter.fieldHasMutableType;
import static de.htwg_konstanz.jia.mdclient.MutabilityAsserter.assertIsMutable;

import java.util.Date;

import org.junit.Test;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 21.11.2012
 */
public final class DirectReturnOfMutableTypeTest {

    /**
     * The reason for mutability is that no defensive copy of {@code date} is
     * made before return. It is acceptable that {@code date} has a mutable type
     * as it is only part of the hidden state of the object and not of the
     * visible state.
     */
    @Test
    public void returnOfMutableTypeWithoutDefensiveCopyLeadsToMutability() {
        assertIsMutable(DirectReturnOfMutableType.class).andNoReasonIsThat(fieldHasMutableType("date", Date.class));
    }

}
