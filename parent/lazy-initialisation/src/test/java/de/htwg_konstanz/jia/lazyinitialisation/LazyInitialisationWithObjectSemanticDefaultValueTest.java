package de.htwg_konstanz.jia.lazyinitialisation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

/**
 * @author Juergen Fickel
 * @version 20.12.2012
 */
public final class LazyInitialisationWithObjectSemanticDefaultValueTest {
    
    @Test
    public void extendedMessageIsExpected() {
        final LazyInitialisationWithObjectSemanticDefaultValue toTest
                = new LazyInitialisationWithObjectSemanticDefaultValue("Foo");
        assertThat(toTest.getExtendedMessage(), is("Foo Hello World!"));
    }

}
