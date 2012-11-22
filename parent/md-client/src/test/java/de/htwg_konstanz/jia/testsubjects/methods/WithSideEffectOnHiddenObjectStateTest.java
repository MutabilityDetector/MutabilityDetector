/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.methods;

import org.junit.Test;

import de.htwg_konstanz.jia.testsubjects.AbstractImmutabilityAsserter;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 22.11.2012
 */
public final class WithSideEffectOnHiddenObjectStateTest extends AbstractImmutabilityAsserter {

    @Test
    public void sideEffectOnHiddenObjectStateDoesNotBreakImmutability() {
        assertIsImmutable(WithSideEffectOnHiddenObjectState.class);
    }

}
