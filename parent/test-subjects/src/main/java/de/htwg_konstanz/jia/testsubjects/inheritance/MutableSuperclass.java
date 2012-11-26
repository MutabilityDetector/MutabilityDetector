/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.inheritance;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 26.11.2012
 */
public class MutableSuperclass {

    private int number;

    public MutableSuperclass(final int aNumber) {
        number = aNumber;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(final int newNumber) {
        number = newNumber;
    }

}
