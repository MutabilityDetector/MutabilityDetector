package de.htwg_konstanz.jia.testsubjects;

import org.junit.Test;
import org.mutabilitydetector.unittesting.MutabilityAssert;

import de.htwg_konstanz.jia.testsubjects.variables.PrivateNonFinalVariables;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz)
 * @version 15.11.2012
 */
public final class PrivateNonFinalVariablesTest {

    @Test
    public void privateVariablesMustNotBeFinal() {
        MutabilityAssert.assertImmutable(PrivateNonFinalVariables.class);
    }

}
