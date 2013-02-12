package de.htwg_konstanz.jia.lazyinitialisation.doublecheck;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 12.02.2013
 */
public final class AliasedIntegerWithDefault {

    private volatile int someNumber;

    public int getSomeNumber() {
        int result = someNumber;
        if (0 == result) {
            synchronized (this) {
                result = someNumber;
                if (0 == result) {
                    result = 42;
                    someNumber = result;
                }
            }
        }
        return result;
    }

}
