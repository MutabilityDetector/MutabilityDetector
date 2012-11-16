package de.htwg_konstanz.jia.testsubjects.lazy;

import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutable;

import org.junit.Test;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 16.11.2012
 */
public final class BasicLazyInitialisationTest {

    @Test
    public void basicLazyInitialisationIsAllowed() {
        assertImmutable(BasicLazyInitialisation.class);
    }

}
