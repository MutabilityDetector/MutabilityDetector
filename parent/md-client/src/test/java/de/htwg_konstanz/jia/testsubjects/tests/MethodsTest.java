/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.tests;

import static de.htwg_konstanz.jia.mdclient.MutabilityAsserter.assertIsMutable;
import static de.htwg_konstanz.jia.mdclient.matcher.Matcher.fieldCanBeReassigned;
import static de.htwg_konstanz.jia.mdclient.matcher.Matcher.methodCausesSideEffect;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;

import org.junit.Test;

import de.htwg_konstanz.jia.testsubjects.methods.BeanLike;
import de.htwg_konstanz.jia.testsubjects.methods.Fakeout;
import de.htwg_konstanz.jia.testsubjects.methods.WithObliqueSideEffect;
import de.htwg_konstanz.jia.testsubjects.methods.WithObviousSideEffect;
import de.htwg_konstanz.jia.testsubjects.methods.WithSideEffectOnHiddenObjectState;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 26.11.2012
 */
public final class MethodsTest {

    @Test
    public void rendersMutableForClassWithClassicSetterMethod() {
        assertIsMutable(BeanLike.class).andOneReasonIsThat(fieldCanBeReassigned("rate", "setRate"));
    }

    @Test
    public void rendersMutableForClassWithUncommonSetterMethod() {
        assertIsMutable(Fakeout.class);
    }

    @Test
    public void rendersMutableForObliqueSideEffect() {
        assertIsMutable(WithObliqueSideEffect.class).andOneReasonIsThat(
                methodCausesSideEffect("getValue", "invokationCounter"));
    }

    @Test
    public void rendersMutableForObviousSideEffect() {
        assertIsMutable(WithObviousSideEffect.class).andOneReasonIsThat(
                methodCausesSideEffect("increaseCounterByOne", "counter"));
    }

    @Test
    public void doesNotRenderMutableForSideEffectOnHiddenObjectState() {
        assertInstancesOf(WithSideEffectOnHiddenObjectState.class, areImmutable());
    }

}
