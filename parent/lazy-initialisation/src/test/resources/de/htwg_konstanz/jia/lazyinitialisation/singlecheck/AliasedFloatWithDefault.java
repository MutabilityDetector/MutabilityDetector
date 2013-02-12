package de.htwg_konstanz.jia.lazyinitialisation.singlecheck;


/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 12.02.2013
 */
public final class AliasedFloatWithDefault {

    private float hash;

    public float hashCodeFloat() {
        float result = hash;
        if (0.0F > result) {
            result = 2342.0F;
            hash = result;
        }
        return result;
    }

}
