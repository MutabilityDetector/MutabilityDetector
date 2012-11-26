/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.inheritance;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 26.11.2012
 */
public final class MutableSubclass extends WeakImmutableSuperclass {

    private int number;

    public MutableSubclass(final int aNumber) {
        super(aNumber);
        number = aNumber;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(final int newNumber) {
        number = newNumber;
    }

}
