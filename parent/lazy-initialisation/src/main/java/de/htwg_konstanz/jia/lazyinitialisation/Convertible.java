package de.htwg_konstanz.jia.lazyinitialisation;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 26.02.2013
 */
public interface Convertible {

    boolean asBoolean();

    byte asByte();

    char asChar();

    short asShort();

    int asInt();

    long asLong();

    float asFloat();

    double asDouble();

    String asString();

    Object asObject();

    <T> T asType(Class<T> targetTypeClass);

}
