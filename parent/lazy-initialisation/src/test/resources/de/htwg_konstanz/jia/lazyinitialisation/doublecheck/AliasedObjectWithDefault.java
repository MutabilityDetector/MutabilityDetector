package de.htwg_konstanz.jia.lazyinitialisation.doublecheck;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 12.02.2013
 */
public final class AliasedObjectWithDefault {

    private volatile Object complexObject;

    public Object getComplexObject() {
        Object result = complexObject;
        if (null == result) {
            synchronized (this) {
                result = complexObject;
                if (null == result) {
                    result = new Object();
                    complexObject = result;
                }
            }
        }
        return result;
    }

}
