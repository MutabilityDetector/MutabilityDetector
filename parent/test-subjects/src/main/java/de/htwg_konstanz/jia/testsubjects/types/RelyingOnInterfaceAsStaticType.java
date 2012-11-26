/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.types;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @date 19.11.2012
 */
public final class RelyingOnInterfaceAsStaticType {

    @SuppressWarnings("unused")
    private final Comparable<Integer> number;

    public RelyingOnInterfaceAsStaticType(final Integer aNumber) {
        number = aNumber;
    }

}
