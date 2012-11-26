/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.tests;

import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areEffectivelyImmutable;

import org.junit.Test;

import de.htwg_konstanz.jia.testsubjects.misc.ImmutableUsingPrivateFieldSettingMethod;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 26.11.2012
 */
public final class MiscellaneousTest {

    @Test
    public void foo() {
        assertInstancesOf(ImmutableUsingPrivateFieldSettingMethod.class, areEffectivelyImmutable());
    }

}
