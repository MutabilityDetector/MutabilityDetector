package de.htwg_konstanz.jia.testsubjects.variables;

import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutable;

import org.junit.Test;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz)
 * @version 15.11.2012
 */
public final class PrivateNonFinalVariablesTest {

    @Test
    public void privateVariablesMustNotBeFinal() {
        assertImmutable(PrivateNonFinalVariables.class);
    }

}
