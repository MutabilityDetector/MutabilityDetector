package de.htwg_konstanz.jia.testsubjects.variables;

/**
 * This class contains mutable and immutable variables which are part
 * of the hidden object state and which are not declared to be
 * {@code final}.
 * 
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 15.11.2012
 */
public final class PrivateNonFinalVariables {

    // static variables
    @SuppressWarnings("unused") private static byte staticByteValue;
    @SuppressWarnings("unused") private static char staticCharacter;
    @SuppressWarnings("unused") private static String staticString;

    // instance variables with primitive type
    @SuppressWarnings("unused") private int number;
    @SuppressWarnings("unused") private char character;
    @SuppressWarnings("unused") private long longNumber;

    // instance variables with reference type
    @SuppressWarnings("unused") private String someString;
    @SuppressWarnings("unused") private StringBuilder mutableString;
    @SuppressWarnings("unused") private Integer numberObject;

}
