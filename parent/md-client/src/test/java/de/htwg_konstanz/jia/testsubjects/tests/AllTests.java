/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 27.11.2012
 */
@RunWith(Suite.class)
@SuiteClasses({
    ConstructorsTest.class,
    InheritanceTest.class,
    LazyInitialisationTest.class,
    MethodsTest.class,
    MiscellaneousTest.class,
    ThisEscapeTest.class,
    TransitivityTest.class,
    TypesTest.class,
    VariablesTest.class
})
public final class AllTests {

    private AllTests() {
        throw new AssertionError();
    }

}
