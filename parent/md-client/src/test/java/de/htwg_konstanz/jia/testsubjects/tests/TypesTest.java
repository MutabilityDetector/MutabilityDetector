/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.tests;

import static de.htwg_konstanz.jia.mdclient.MutabilityAsserter.assertIsMutable;
import static de.htwg_konstanz.jia.mdclient.matcher.Matcher.abstractTypeIsInherentlyMutable;

import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;

import org.junit.Test;

import de.htwg_konstanz.jia.testsubjects.types.AbstractClass;
import de.htwg_konstanz.jia.testsubjects.types.ImmutableEnumType;
import de.htwg_konstanz.jia.testsubjects.types.MarkerInterface;
import de.htwg_konstanz.jia.testsubjects.types.RelyingOnInterfaceAsStaticType;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 26.11.2012
 */
public final class TypesTest {

    @Test
    public void doesNotRenderMutableForUsingInterfaceAsStaticType() {
        assertInstancesOf(RelyingOnInterfaceAsStaticType.class, areImmutable());
    }

    @Test
    public void doesNotRenderMutableForEnumWithGetterForInt() {
        assertInstancesOf(ImmutableEnumType.class, areImmutable());
    }

    @Test
    public void rendersMutableForMarkerInterface() {
        assertIsMutable(MarkerInterface.class).andTheReasonIsThat(abstractTypeIsInherentlyMutable());
    }

    @Test
    public void rendersMutableForAbstractClass() {
        assertIsMutable(AbstractClass.class).andOneReasonIsThat(abstractTypeIsInherentlyMutable());
    }

}
