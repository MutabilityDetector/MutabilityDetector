/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.escape;

import org.junit.Test;

import de.htwg_konstanz.jia.testsubjects.MutabilityAsserter;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 20.11.2012
 */
public final class DirectAssignmentOfThisToVisibleVariableTest extends MutabilityAsserter {

    public DirectAssignmentOfThisToVisibleVariableTest() {
        super(DirectAssignmentOfThisToVisibleVariable.class);
    }

    @Test
    public void directlyAssigningThisToVisibleInstanceVariableLeadsToMutableClass() {
        assertIsMutable();
    }

    @Test
    public void reasonForMutabilityIsEscapingThisReference() {
        assertOneReasonIsThat(analysedClass(), leaksItsThisReference());
    }

}
