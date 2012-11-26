package de.htwg_konstanz.jia.testsubjects.inheritance;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 16.11.2012
 */
public class WeakImmutableSuperclass {

    private final int number;

    public WeakImmutableSuperclass(final int aNumber) {
        number = aNumber;
    }

    public int getNumber() {
        return number;
    }

}
