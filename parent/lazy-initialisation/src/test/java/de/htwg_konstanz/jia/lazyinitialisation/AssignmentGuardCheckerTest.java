/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.objectweb.asm.tree.ClassNode;

import de.htwg_konstanz.jia.lazyinitialisation.AssignmentGuardFinder.AssignmentGuardChecker;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 06.03.2013
 */
public final class AssignmentGuardCheckerTest {

    @Test(expected = NullPointerException.class)
    public void factoryMethodAssertsThatVariableNameIsNotNull() {
        AssignmentGuardChecker.newInstance(null, ConvenienceClassNode.newInstance(null));
    }

    @Test
    public void factoryMethodAssertsThatVariableNameIsNotEmpty() {
        final String exp = "Argument 'variableName' must not be empty!";
        try {
            AssignmentGuardChecker.newInstance("", ConvenienceClassNode.newInstance(new ClassNode()));
            fail(String.format("Expected IllegalArgumentException with message '%s'.", exp));
        } catch (final IllegalArgumentException e) {
            assertThat(e.getMessage(), is(equalTo(exp)));
        }
    }

    @Test
    public void factoryMethodAssertsThatClassNodeIsNotNull() {
        final String exp = "Argument 'classNode' must not be null!";
        try {
            AssignmentGuardChecker.newInstance("foo", null);
            fail(String.format("Expected NullPointerException with message '%s'.", exp));
        } catch (final NullPointerException e) {
            assertThat(e.getMessage(), is(equalTo(exp)));
        }
    }

}
