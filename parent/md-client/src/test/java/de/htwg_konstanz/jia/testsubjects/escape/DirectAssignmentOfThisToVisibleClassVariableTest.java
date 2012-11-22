/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.escape;

import static de.htwg_konstanz.jia.mdclient.matcher.Matcher.classLeaksItsThisReference;
import static de.htwg_konstanz.jia.mdclient.MutabilityAsserter.assertIsMutable;

import org.junit.Test;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 21.11.2012
 */
public final class DirectAssignmentOfThisToVisibleClassVariableTest {

    @Test
    public void directlyAssigningThisToVisibleClassVariableLeadsToMutableClass() {
        assertIsMutable(DirectAssignmentOfThisToVisibleClassVariable.class)
            .andOneReasonIsThat(classLeaksItsThisReference());
    }

}
