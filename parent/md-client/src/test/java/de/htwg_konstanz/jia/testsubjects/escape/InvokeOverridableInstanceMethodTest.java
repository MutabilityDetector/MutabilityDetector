package de.htwg_konstanz.jia.testsubjects.escape;

import org.junit.Test;

import de.htwg_konstanz.jia.testsubjects.AbstractMutabilityAsserter;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 21.11.2012
 */
public final class InvokeOverridableInstanceMethodTest extends AbstractMutabilityAsserter {

    public InvokeOverridableInstanceMethodTest() {
        super(InvokeOverridableInstanceMethod.class);
    }

    @Test
    public void invokingAnOverridableInstanceMethodFromWithinAConstructorBreaksImmutability() {
        assertIsMutable();
    }

    @Test
    public void reasonForMutabilityIsEscapingThisReference() {
        assertTheReasonIsThat(classLeaksItsThisReference());
    }

}
