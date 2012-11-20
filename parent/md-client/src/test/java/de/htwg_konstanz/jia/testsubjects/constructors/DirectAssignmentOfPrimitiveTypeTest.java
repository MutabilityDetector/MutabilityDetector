/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.constructors;

import org.junit.Test;

import de.htwg_konstanz.jia.testsubjects.ImmutabilityAsserter;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 20.11.2012
 */
public final class DirectAssignmentOfPrimitiveTypeTest extends ImmutabilityAsserter {

    @Test
    public void directAssignmentOfPrimitiveTypeDoesNotBreakImmutability() {
        assertImmutable(DirectAssignmentOfPrimitiveType.class);
    }

}
