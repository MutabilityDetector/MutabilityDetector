package de.htwg_konstanz.jia.testsubjects.variables;

/**
 * This class contains mutable and immutable variables which are part
 * of the visible object state but which are not declared to be
 * {@code final}.
 * 
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 15.11.2012
 */
public final class VisibleNonFinalVariables {

    // static variables
    static byte staticByteValue;
    protected static char staticCharacter;
    public static String staticString;

    // instance variables with primitive type
    int number;
    protected char character;
    public long longNumber;

    // instance variables with reference type
    String someString;
    protected StringBuilder mutableString;
    public Integer numberObject;

}
