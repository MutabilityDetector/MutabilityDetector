package de.htwg_konstanz.jia.lazyinitialisation.singlecheck;


/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 14.02.2013
 */
public final class ObjectWithDefault {

    private Object hash = null;

    public Object hashCodeObject() {
        if (null == hash) {
            hash = new Object();
        }
        return hash;
    }

}
