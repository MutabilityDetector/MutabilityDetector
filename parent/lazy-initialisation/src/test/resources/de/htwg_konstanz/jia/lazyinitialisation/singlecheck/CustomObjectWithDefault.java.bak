package de.htwg_konstanz.jia.lazyinitialisation.singlecheck;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 16.11.2012
 */
public final class CustomObjectWithDefault {

    private static final class SomeObject {
        private SomeObject() {
            super();
        }
        public static SomeObject getInstance() {
            return new SomeObject();
        }
    }

    private SomeObject someObject;

    public SomeObject hashCodeSomeObject() {
        if (null == someObject) {
            someObject = SomeObject.getInstance();;
        }
        return someObject;
    }

}
