/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.escape;

import org.junit.Test;

import de.htwg_konstanz.jia.testsubjects.AbstractMutabilityAsserter;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 20.11.2012
 */
public final class DirectAssignmentOfThisToVisibleInstanceVariableTest extends AbstractMutabilityAsserter {

    public DirectAssignmentOfThisToVisibleInstanceVariableTest() {
        super(DirectAssignmentOfThisToVisibleInstanceVariable.class);
    }

    @Test
    public void directlyAssigningThisToVisibleInstanceVariableLeadsToMutableClass() {
        assertIsMutable();
    }

    @Test
    public void reasonForMutabilityIsEscapingThisReference() {
        assertOneReasonIsThat(classLeaksItsThisReference());
    }

}
