package de.htwg_konstanz.jia.lazyinitialisation.synch;

public final class ObjectWithDefault {

    private Object complexObject;

    public synchronized Object getComplexObject() {
        if (null == complexObject) {
            complexObject = new Object();
        }
        return complexObject;
    }

}
