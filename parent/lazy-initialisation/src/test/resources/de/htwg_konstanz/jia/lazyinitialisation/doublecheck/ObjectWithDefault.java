package de.htwg_konstanz.jia.lazyinitialisation.doublecheck;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 12.02.2013
 */
public final class ObjectWithDefault {

    private volatile Object complexObject;

    public Object getComplexObject() {
        if (null == complexObject) {
            synchronized (this) {
                if (null == complexObject) {
                    complexObject = new Object();
                }
            }
        }
        return complexObject;
    }

}
