package org.mutabilitydetector.checkers.settermethod;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 26.02.2013
 */
public interface UnknownTypeValue {

    enum Default {
        NULL,
        UNKNOWN_PRIMITIVE,
        UNKNOWN_REFERENCE
    } // enum Default

    boolean asBoolean();

    char asChar();

    boolean isBoolean();

    boolean isByte();

    boolean isChar();

    boolean isShort();

    boolean isInt();

    boolean isLong();

    boolean isFloat();

    boolean isDouble();

    boolean isString();

    boolean isObject();

    boolean isZero();

    boolean isNotZero();

    boolean isNull();

    boolean isNotNull();

    boolean isUnknownPrimitive();

    boolean isUnknownReference();

}
