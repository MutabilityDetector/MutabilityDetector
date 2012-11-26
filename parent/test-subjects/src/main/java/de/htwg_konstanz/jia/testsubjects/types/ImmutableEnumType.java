/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.types;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 26.11.2012
 */
public enum ImmutableEnumType {
    INSTANCE(23);

    private final int number;
    
    private ImmutableEnumType(final int aNumber) {
        number = aNumber;
    }

    public int getNumber() {
        return number;
    }

}
