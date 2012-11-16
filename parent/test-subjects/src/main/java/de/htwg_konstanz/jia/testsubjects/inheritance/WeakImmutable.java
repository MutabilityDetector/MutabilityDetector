package de.htwg_konstanz.jia.testsubjects.inheritance;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 16.11.2012
 */
public class WeakImmutable {

    private final int number;

    public WeakImmutable(final int aNumber) {
        number = aNumber;
    }

    public int getNumber() {
        return number;
    }

}
