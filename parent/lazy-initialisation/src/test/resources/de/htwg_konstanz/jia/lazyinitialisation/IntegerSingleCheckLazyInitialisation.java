package de.htwg_konstanz.jia.lazyinitialisation;


/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 16.11.2012
 */
public final class IntegerSingleCheckLazyInitialisation {

    private static final String ID = "Foo";

    private int hash;

    @Override
    public int hashCode() {
        if (0 == hash) {
            hash = 2342;
        }
        return hash;
    }

}
