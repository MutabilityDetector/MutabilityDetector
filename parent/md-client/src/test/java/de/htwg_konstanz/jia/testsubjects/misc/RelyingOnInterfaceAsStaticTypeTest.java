/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.misc;

import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutable;

import org.junit.Test;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 19.11.2012
 */
public final class RelyingOnInterfaceAsStaticTypeTest {

    @Test
    public void usingInterfacesDoesNotBreakImmutability() {
        assertImmutable(RelyingOnInterfaceAsStaticType.class);
    }

}
