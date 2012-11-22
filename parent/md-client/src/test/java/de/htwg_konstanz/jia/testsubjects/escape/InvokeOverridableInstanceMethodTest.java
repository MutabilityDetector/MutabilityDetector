package de.htwg_konstanz.jia.testsubjects.escape;

import static de.htwg_konstanz.jia.mdclient.matcher.Matcher.classAllowsInheritance;
import static de.htwg_konstanz.jia.mdclient.matcher.Matcher.classLeaksItsThisReference;
import static de.htwg_konstanz.jia.mdclient.MutabilityAsserter.assertIsMutable;

import org.junit.Test;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 21.11.2012
 */
public final class InvokeOverridableInstanceMethodTest {

    /**
     * 
     */
    @Test
    public void invokingAnOverridableInstanceMethodFromWithinAConstructorBreaksImmutability() {
        assertIsMutable(InvokeOverridableInstanceMethod.class)
            .andTheReasonIsThat(classLeaksItsThisReference())
            .andNoReasonIsThat(classAllowsInheritance());
    }

}
