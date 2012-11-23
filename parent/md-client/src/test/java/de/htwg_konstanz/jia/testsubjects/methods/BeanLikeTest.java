/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.methods;

import static de.htwg_konstanz.jia.mdclient.matcher.Matcher.fieldCanBeReassigned;
import static de.htwg_konstanz.jia.mdclient.MutabilityAsserter.assertIsMutable;

import org.junit.Test;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 19.11.2012
 */
public final class BeanLikeTest {

    @Test
    public void classWithSetterMethodIsMutable() {
        assertIsMutable(BeanLike.class).andOneReasonIsThat(fieldCanBeReassigned("rate", "setRate"));
    }

}
