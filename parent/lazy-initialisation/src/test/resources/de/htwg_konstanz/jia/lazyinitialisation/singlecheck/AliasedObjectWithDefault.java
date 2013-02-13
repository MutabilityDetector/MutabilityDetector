package de.htwg_konstanz.jia.lazyinitialisation.singlecheck;


/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 13.02.2013
 */
public final class AliasedObjectWithDefault {

    private Object hash;

    public Object hashCodeObject() {
        Object result = hash;
        if (null == result) {
            result = new Object();
            hash = result;
        }
        return result;
    }

}
