/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.escape;

import org.junit.Test;

import de.htwg_konstanz.jia.testsubjects.AbstractMutabilityAsserter;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 21.11.2012
 */
public final class DirectAssignmentOfThisToVisibleClassVariableTest extends AbstractMutabilityAsserter {

    public DirectAssignmentOfThisToVisibleClassVariableTest() {
        super(DirectAssignmentOfThisToVisibleClassVariable.class);
    }

    @Test
    public void directlyAssigningThisToVisibleClassVariableLeadsToMutableClass() {
        assertIsMutable();
    }

    @Test
    public void reasonForMutabilityIsEscapingThisReference() {
        assertOneReasonIsThat(classLeaksItsThisReference());
    }

}
