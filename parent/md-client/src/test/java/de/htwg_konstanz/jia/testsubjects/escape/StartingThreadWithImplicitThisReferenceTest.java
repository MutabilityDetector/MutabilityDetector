package de.htwg_konstanz.jia.testsubjects.escape;

import org.junit.Test;

import de.htwg_konstanz.jia.testsubjects.AbstractMutabilityAsserter;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 21.11.2012
 */
public final class StartingThreadWithImplicitThisReferenceTest extends AbstractMutabilityAsserter {

    public StartingThreadWithImplicitThisReferenceTest() {
        super(StartingThreadWithImplicitThisReference.class);
    }

    @Test
    public void startingThatThreadMakesClassMutable() {
        assertIsMutable();
    }

    @Test
    public void reasonForMutabilityIsEscapingOfThisReference() {
        assertTheReasonIsThat(classLeaksItsThisReference());
    }

}
