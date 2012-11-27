/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.tests;

import static de.htwg_konstanz.jia.mdclient.MutabilityAsserter.assertIsMutable;
import static de.htwg_konstanz.jia.mdclient.matcher.Matcher.fieldHasMutableType;
import static de.htwg_konstanz.jia.mdclient.matcher.Matcher.classPublishesNonFinalField;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areEffectivelyImmutable;

import java.util.Date;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import de.htwg_konstanz.jia.mdclient.AnalysisResultAsserter;
import de.htwg_konstanz.jia.testsubjects.variables.*;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 27.11.2012
 */
public final class VariablesTest {

    @Test
    public void doesNotRenderMutableForMutableVariablesOfHiddenObjectState() {
        assertInstancesOf(PrivateNonFinalVariables.class, areEffectivelyImmutable());
    }

    @Test
    public void doesNotRenderMutableForIntArrayWhichIsPartOfHiddenObjectState() {
        assertInstancesOf(WithPrivateFinalIntArray.class, areEffectivelyImmutable());
    }

    @Test
    public void doesNotRenderMutableForDateArrayWhichIsPartOfHiddenObjectState() {
        assertInstancesOf(WithPrivateFinalDateArray.class, areEffectivelyImmutable());
    }

    @Test
    public void rendersMutableForDirectlyPublishingMutableType() {
        assertIsMutable(DirectReturnOfMutableType.class).andNoReasonIsThat(fieldHasMutableType("date", Date.class));
    }

    @Test
    public void doesNotRenderMutableForReturningDefensivelyCopiedMutableField() {
        assertInstancesOf(IndirectReturnOfMutableType.class, areImmutable());
    }

    @Test
    public void rendersMutableForVisibleNonFinalVariables() {
        AnalysisResultAsserter resultAsserter = assertIsMutable(VisibleNonFinalVariables.class);
        for (final String expectedName : getExpectedNamesOfNonFinalVariables()) {
            resultAsserter = resultAsserter.andOneReasonIsThat(classPublishesNonFinalField(expectedName));
        }
    }

    private Set<String> getExpectedNamesOfNonFinalVariables() {
        final ImmutableSet.Builder<String> result = new ImmutableSet.Builder<String>();
        result.add("staticByteValue").add("staticCharacter").add("staticString").add("number").add("character");
        result.add("longNumber").add("someString").add("mutableString").add("numberObject");
        return result.build();
    }

}
