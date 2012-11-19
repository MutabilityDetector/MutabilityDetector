/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.methods;

/**
 * This class provides setter and getter methods for instance fields.
 * 
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 19.11.2012
 */
public final class BeanLike {

    private final int value;
    private float rate;

    public BeanLike(final float aRate, final int aValue) {
        rate = aRate;
        value = aValue;
    }

    public int getValue() {
        return value;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(final float newRate) {
        rate = newRate;
    }

}
