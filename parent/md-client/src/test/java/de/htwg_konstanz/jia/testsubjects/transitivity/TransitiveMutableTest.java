/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.transitivity;

import static de.htwg_konstanz.jia.mdclient.AnalysisResultAsserter.fieldHasMutableType;
import static de.htwg_konstanz.jia.mdclient.MutabilityAsserter.assertIsMutable;

import org.junit.Test;

import de.htwg_konstanz.jia.testsubjects.transitivity.TransitiveMutable.NestedMutable;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 19.11.2012
 */
public final class TransitiveMutableTest {

    @Test
    public void mutabilityIsTransitive() {
        assertIsMutable(TransitiveMutable.class).andTheReasonIsThat(fieldHasMutableType("nested", NestedMutable.class));
    }

}
