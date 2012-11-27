/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.tests;

import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areEffectivelyImmutable;

import org.junit.Test;

import de.htwg_konstanz.jia.testsubjects.lazy.BasicLazyInitialisation;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 27.11.2012
 */
public final class LazyInitialisationTest {

    @Test
    public void doesNotRenderMutableForBasicLazyInitialisation() {
        assertInstancesOf(BasicLazyInitialisation.class, areEffectivelyImmutable());
    }

}
