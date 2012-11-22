package de.htwg_konstanz.jia.testsubjects.escape;

import org.junit.Test;

import static de.htwg_konstanz.jia.mdclient.matcher.Matcher.classLeaksItsThisReference;
import static de.htwg_konstanz.jia.mdclient.MutabilityAsserter.assertIsMutable;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 21.11.2012
 */
public final class CallingConstructorWithThisTest {

    @Test
    public void callingAnAlienConstructorWithThisCausesMutability() {
        assertIsMutable(CallingConstructorWithThis.class).andTheReasonIsThat(classLeaksItsThisReference());
    }

}
