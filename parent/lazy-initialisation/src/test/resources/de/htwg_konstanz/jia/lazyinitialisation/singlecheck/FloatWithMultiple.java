package de.htwg_konstanz.jia.lazyinitialisation.singlecheck;


/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 15.02.2013
 */
public final class FloatWithMultiple {

    private float hash = 0.0F;

    public FloatWithMultiple() {
        hash = -1.0F;
    }

    public FloatWithMultiple(final float initial) {
        hash = initial;
    }

    public FloatWithMultiple(final String foo) {
        hash = 23.0F;
    }

    public float hashCodeFloat() {
        if (0.0F == hash) {
            hash = 2342.0F;
        }
        return hash;
    }

}
