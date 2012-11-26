package de.htwg_konstanz.jia.testsubjects.escapedthis.unsafe;

import de.htwg_konstanz.jia.testsubjects.escapedthis.GiveMeYourThisReference;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 21.11.2012
 */
public class InvokeOverridableInstanceMethod {

    public InvokeOverridableInstanceMethod() {
        super();
    }

    public void overridable() {
        new GiveMeYourThisReference(this);
    }

}
