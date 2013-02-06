package de.htwg_konstanz.jia.lazyinitialisation.singlecheck;


/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 16.11.2012
 */
public final class FloatWithDefault {

    private float hash;

    public float hashCodeFloat() {
        if (0.0F > hash) {
            hash = 2342.0F;
        }
        return hash;
    }

}
