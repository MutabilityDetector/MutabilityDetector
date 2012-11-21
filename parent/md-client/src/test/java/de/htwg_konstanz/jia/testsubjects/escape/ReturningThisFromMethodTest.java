package de.htwg_konstanz.jia.testsubjects.escape;

import org.junit.Test;

import de.htwg_konstanz.jia.testsubjects.AbstractMutabilityAsserter;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 21.11.2012
 */
public final class ReturningThisFromMethodTest extends AbstractMutabilityAsserter {

    public ReturningThisFromMethodTest() {
        super(ReturningThisFromMethod.class);
    }

    @Test
    public void returningThisFromStaticMethodBreakImmutability() {
        assertIsMutable();
    }

    @Test
    public void reasonForMutabilityIsEscapingThisReference() {
        assertOneReasonIsThat(classLeaksItsThisReference());
    }

}
