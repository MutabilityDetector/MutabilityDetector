/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.tests;

import static de.htwg_konstanz.jia.mdclient.MutabilityAsserter.assertIsMutable;
import static de.htwg_konstanz.jia.mdclient.matcher.Matcher.fieldHasMutableType;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;

import org.junit.Test;

import de.htwg_konstanz.jia.testsubjects.transitivity.TransitiveImmutable;
import de.htwg_konstanz.jia.testsubjects.transitivity.TransitiveMutable;
import de.htwg_konstanz.jia.testsubjects.transitivity.TransitiveMutable.NestedMutable;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 27.11.2012
 */
public final class TransitivityTest {

    @Test
    public void rendersMutableForUsageOfMutableType() {
        assertIsMutable(TransitiveMutable.class).andTheReasonIsThat(fieldHasMutableType("nested", NestedMutable.class));
    }

    @Test
    public void doesNotRenderMutableForUsageOfImmutableType() {
        assertInstancesOf(TransitiveImmutable.class, areImmutable());
    }

}
