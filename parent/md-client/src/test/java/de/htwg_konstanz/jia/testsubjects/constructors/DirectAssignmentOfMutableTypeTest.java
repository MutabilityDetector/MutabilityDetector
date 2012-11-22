/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.constructors;

import static de.htwg_konstanz.jia.mdclient.matcher.Matcher.fieldHasMutableType;
import static de.htwg_konstanz.jia.mdclient.MutabilityAsserter.assertIsMutable;

import java.util.Date;

import org.junit.Test;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 19.11.2012
 */
public final class DirectAssignmentOfMutableTypeTest {

    /**
     * The reason for mutability is that no defensive copy of {@code date} is
     * made before assignment. It is acceptable that {@code date} has a mutable
     * type as it is only part of the hidden state of the object.
     */
    @Test
    public void directAssignmentOfMutableTypeLeadsToMutableClass() {
        assertIsMutable(DirectAssignmentOfMutableType.class).andNoReasonIsThat(fieldHasMutableType("date", Date.class));
    }

}
