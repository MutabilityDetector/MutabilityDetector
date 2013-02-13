package de.htwg_konstanz.jia.lazyinitialisation.singlecheck;


/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 13.02.2013
 */
public final class AliasedStringWithDefault {

    private String hash;

    public String hashCodeString() {
        String result = hash;
        if (null == result) {
            result = "Hallo Welt";
            hash = result;
        }
        return result;
    }

}
