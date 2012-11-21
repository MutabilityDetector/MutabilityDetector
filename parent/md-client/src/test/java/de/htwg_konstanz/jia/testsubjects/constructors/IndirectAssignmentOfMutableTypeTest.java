/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.constructors;

import org.junit.Test;

import de.htwg_konstanz.jia.testsubjects.AbstractImmutabilityAsserter;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 21.11.2012
 */
public final class IndirectAssignmentOfMutableTypeTest extends AbstractImmutabilityAsserter {

    @Test
    public void indirectAssignmentOfMutableTypePreservesImmutability() {
        assertIsImmutable(IndirectAssignmentOfMutableType.class);
    }

}
