/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.transitivity;

import org.junit.Test;

import de.htwg_konstanz.jia.testsubjects.MutabilityAsserter;
import de.htwg_konstanz.jia.testsubjects.transitivity.TransitiveMutable.NestedMutable;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 19.11.2012
 */
public final class TransitiveMutableTest extends MutabilityAsserter {

    public TransitiveMutableTest() {
        super(TransitiveMutable.class);
    }

    @Test
    public void mutabilityIsTransitive() {
        assertIsMutable();
    }

    @Test
    public void classIsMutableBecauseOfExpectedReason() {
        assertTheReasonIsThat(field("nested"), hasMutableType(NestedMutable.class));
    }

}
