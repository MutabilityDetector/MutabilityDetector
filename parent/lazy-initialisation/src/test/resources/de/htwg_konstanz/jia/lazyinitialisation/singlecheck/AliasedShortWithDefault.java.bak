package de.htwg_konstanz.jia.lazyinitialisation.singlecheck;


/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 13.02.2013
 */
public final class AliasedShortWithDefault {

    private short hash;

    public short hashCodeShort() {
        short result = hash;
        if (0 == result) {
            result = (short) 128;
            hash = result;
        }
        return result;
    }

}
