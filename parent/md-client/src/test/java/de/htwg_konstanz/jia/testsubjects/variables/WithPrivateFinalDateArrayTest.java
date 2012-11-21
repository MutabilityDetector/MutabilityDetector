/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.variables;

import org.junit.Test;

import de.htwg_konstanz.jia.testsubjects.AbstractImmutabilityAsserter;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 20.11.2012
 */
public final class WithPrivateFinalDateArrayTest extends AbstractImmutabilityAsserter {

    @Test
    public void privateFinalDateArrayDoesNotBreakImmutability() {
        assertIsImmutable(WithPrivateFinalDateArray.class);
    }

}
