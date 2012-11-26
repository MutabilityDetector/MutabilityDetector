/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.tests;

import static de.htwg_konstanz.jia.mdclient.MutabilityAsserter.assertIsMutable;
import static de.htwg_konstanz.jia.mdclient.matcher.Matcher.methodCausesSideEffect;
import static de.htwg_konstanz.jia.mdclient.matcher.Matcher.abstractTypeIsInherentlyMutable;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutable;

import org.junit.Test;

import de.htwg_konstanz.jia.testsubjects.inheritance.ExtendingMutableSuperclass;
import de.htwg_konstanz.jia.testsubjects.inheritance.ImmutableSubclass;
import de.htwg_konstanz.jia.testsubjects.inheritance.MutableSubclass;
import de.htwg_konstanz.jia.testsubjects.inheritance.SubclassOfAbstractClass;
import de.htwg_konstanz.jia.testsubjects.inheritance.WeakImmutableSuperclass;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 26.11.2012
 */
public final class InheritanceTest {

    @Test
    public void doesNotRenderMutableForWeakImmutableSuperclass() {
        assertImmutable(WeakImmutableSuperclass.class);
    }

    @Test
    public void doesNotRenderMutableForImmutableSubclass() {
        assertImmutable(ImmutableSubclass.class);
    }

    @Test
    public void rendersMutableForMutableSubclass() {
        assertIsMutable(MutableSubclass.class).andOneReasonIsThat(methodCausesSideEffect("setNumber", "number"));
    }

    @Test
    public void rendersMutableForSubclassOfMutableSuperclass() {
        assertIsMutable(ExtendingMutableSuperclass.class);
    }

    @Test
    public void rendersMutableForSubclassOfAbstractSuperclass() {
        assertIsMutable(SubclassOfAbstractClass.class).andOneReasonIsThat(abstractTypeIsInherentlyMutable());
    }

}
