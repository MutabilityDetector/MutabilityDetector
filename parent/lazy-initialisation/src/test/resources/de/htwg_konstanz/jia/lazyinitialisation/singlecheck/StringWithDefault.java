package de.htwg_konstanz.jia.lazyinitialisation.singlecheck;


/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 16.11.2012
 */
public final class StringWithDefault {

    private String hash;

    public String hashCodeString() {
        if (null == hash) {
            hash = "Hash code";
        }
        return hash;
    }

}
