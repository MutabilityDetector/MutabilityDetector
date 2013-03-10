package de.htwg_konstanz.jia.lazyinitialisation;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 26.02.2013
 */
public interface UnknownTypeValue extends TypeInformation, Convertible {

    enum Default {
        NULL,
        UNKNOWN_PRIMITIVE,
        UNKNOWN_REFERENCE
    }
    
}
