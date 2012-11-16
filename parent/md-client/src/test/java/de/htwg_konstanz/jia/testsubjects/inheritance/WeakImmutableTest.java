package de.htwg_konstanz.jia.testsubjects.inheritance;

import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutable;

import org.junit.Test;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 16.11.2012
 */
public final class WeakImmutableTest {

    @Test
    public void weakImmutableClassIsImmutable() {
        assertImmutable(WeakImmutable.class);
    }

}
