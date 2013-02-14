package de.htwg_konstanz.jia.lazyinitialisation.singlecheck;


/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 14.02.2013
 */
public final class FloatWithSemantic {

    private float hash = -1.0F;

    public float hashCodeFloat() {
        if (0.0F == hash) {
            hash = 2342.0F;
        }
        return hash;
    }

}
