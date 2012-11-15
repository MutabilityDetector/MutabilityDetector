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
    private static byte staticByteValue;
    private static char staticCharacter;
    private static String staticString;

    // instance variables with primitive type
    private int number;
    private char character;
    private long longNumber;

    // instance variables with reference type
    private String someString;
    private StringBuilder mutableString;
    private Integer numberObject;

}
