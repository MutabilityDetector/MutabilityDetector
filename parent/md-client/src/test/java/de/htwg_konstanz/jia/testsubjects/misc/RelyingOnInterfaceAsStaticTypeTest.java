/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.misc;

import org.junit.Test;

import de.htwg_konstanz.jia.testsubjects.ImmutabilityAsserter;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 19.11.2012
 */
public final class RelyingOnInterfaceAsStaticTypeTest extends ImmutabilityAsserter {

    @Test
    public void usingInterfacesDoesNotBreakImmutability() {
        assertImmutable(RelyingOnInterfaceAsStaticType.class);
    }

}
