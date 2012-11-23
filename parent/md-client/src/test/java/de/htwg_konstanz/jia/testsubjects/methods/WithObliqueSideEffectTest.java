/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.methods;

import static de.htwg_konstanz.jia.mdclient.matcher.Matcher.methodCausesSideEffect;
import static de.htwg_konstanz.jia.mdclient.MutabilityAsserter.assertIsMutable;

import org.junit.Test;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 22.11.2012
 */
public final class WithObliqueSideEffectTest {

    @Test
    public void anObliqueSideEffectBreaksImmutability() {
        assertIsMutable(WithObliqueSideEffect.class)
            .andOneReasonIsThat(methodCausesSideEffect("getValue", "invokationCounter"));
    }


}
