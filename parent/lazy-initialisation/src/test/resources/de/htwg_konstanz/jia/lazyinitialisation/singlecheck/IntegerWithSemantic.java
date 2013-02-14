package de.htwg_konstanz.jia.lazyinitialisation.singlecheck;


/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 14.02.2013
 */
public final class IntegerWithSemantic {

    private static final class IntValueHolder {
        public int getValue() {
            return 1;
        }
    }

    private int hash = 0;

    @Override
    public int hashCode() {
        if (0 == hash) {
            hash = 2342;
        }
        return hash;
    }

}
