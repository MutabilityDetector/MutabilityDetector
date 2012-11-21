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
public final class InvokeAlienMethodWithThisTest extends AbstractMutabilityAsserter {

    public InvokeAlienMethodWithThisTest() {
        super(InvokeAlienMethodWithThis.class);
    }

    @Test
    public void invokingAnAlienMethodWithThisLeadsToMutableClass() {
        assertIsMutable();
    }

    @Test
    public void onlyExpectedReasonsOccurred() {
        assertTheReasonIsThat(classLeaksItsThisReference());
    }

}
