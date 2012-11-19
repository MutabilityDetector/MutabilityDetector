/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.transitivity;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 19.11.2012
 */
public final class TransitiveMutable {

    public static final class NestedMutable {
        
        private int value;
        
        public NestedMutable(final int aValue) {
            value = aValue;
        }

        /**
         * Gets the value current value. With each invokation the value is
         * increased by one (side effekt).
         * 
         * @return the current value.
         */
        public int getValue() {
            return value++;
        }

    }

    private final NestedMutable nested;

    public TransitiveMutable(final int value) {
        nested = new NestedMutable(value);
    }

    public NestedMutable getWithValue() {
        return nested;
    }

}
