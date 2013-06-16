/**
 * 
 */
package org.mutabilitydetector.checkers.settermethod;

import org.mutabilitydetector.checkers.settermethod.Opcode.Category;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 07.12.2012
 */
public interface OpcodeInformation {

    int asInt();

    Category category();

    UnknownTypeValue stackValue();

    String description();

}
