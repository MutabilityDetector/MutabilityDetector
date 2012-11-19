/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.transitivity;

/**
 * 
 * 
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 19.11.2012
 */
public final class TransitiveImmutable {

    public static final class NestedImmutable {
        private final int value;
        
        public NestedImmutable(final int aValue) {
            value = aValue;
        }

        public int getValue() {
            return value;
        }
    }

    private final NestedImmutable nested;

    public TransitiveImmutable(final int value) {
        nested = new NestedImmutable(value);
    }

    public NestedImmutable getWithValue() {
        return nested;
    }

}
