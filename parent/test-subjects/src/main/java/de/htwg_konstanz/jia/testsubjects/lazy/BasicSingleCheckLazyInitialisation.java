package de.htwg_konstanz.jia.testsubjects.lazy;


/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 16.11.2012
 */
public final class BasicSingleCheckLazyInitialisation {

    private volatile int hash;

    @Override
    public int hashCode() {
        if (0 == hash) {
            hash = 2342;
        }
        return hash;
    }

}
