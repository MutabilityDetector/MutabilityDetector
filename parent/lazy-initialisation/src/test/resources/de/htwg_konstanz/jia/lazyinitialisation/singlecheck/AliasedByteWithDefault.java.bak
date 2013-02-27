package de.htwg_konstanz.jia.lazyinitialisation.singlecheck;


/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 13.02.2013
 */
public final class AliasedByteWithDefault {

    private byte hash;

    public byte hashCodeByte() {
        byte result = hash;
        if (0 == result) {
            result = (byte) 128;
            hash = result;
        }
        return result;
    }

}
