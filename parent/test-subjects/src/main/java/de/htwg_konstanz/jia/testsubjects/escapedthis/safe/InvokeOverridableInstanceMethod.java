package de.htwg_konstanz.jia.testsubjects.escapedthis.safe;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 21.11.2012
 */
public class InvokeOverridableInstanceMethod {

    @SuppressWarnings("unused")
    public InvokeOverridableInstanceMethod() {
        super();
        final Object temp = overridable();
    }

    public Object overridable() {
        return this;
    }

}
