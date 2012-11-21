package de.htwg_konstanz.jia.testsubjects.lazy;

import org.junit.Test;

import de.htwg_konstanz.jia.testsubjects.AbstractImmutabilityAsserter;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 16.11.2012
 */
public final class BasicLazyInitialisationTest extends AbstractImmutabilityAsserter {

    @Test
    public void basicLazyInitialisationIsAllowed() {
        assertIsImmutable(BasicLazyInitialisation.class);
    }

}
