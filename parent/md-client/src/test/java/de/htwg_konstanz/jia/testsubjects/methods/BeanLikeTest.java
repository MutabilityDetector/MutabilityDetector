/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.methods;

import org.junit.Test;
import de.htwg_konstanz.jia.testsubjects.AbstractMutabilityAsserter;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 19.11.2012
 */
public final class BeanLikeTest extends AbstractMutabilityAsserter {

    public BeanLikeTest() {
        super(BeanLike.class);
    }

    @Test
    public void classWithSetterMethodIsMutable() {
        assertIsMutable();
    }

    @Test
    public void classIsMutableBecauseFieldCanBeReassigned() {
        assertOneReasonIsThat(fieldCanBeReassigned("rate", "setRate"));
    }

}
