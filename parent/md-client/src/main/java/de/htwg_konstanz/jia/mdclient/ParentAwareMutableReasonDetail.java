/**
 * 
 */
package de.htwg_konstanz.jia.mdclient;

import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.Reason;
import org.mutabilitydetector.locations.CodeLocation;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 22.11.2012
 */
public interface ParentAwareMutableReasonDetail {

    IsImmutable isImmutable();

    String dottedClassName();

    Reason reason();

    CodeLocation<?> codeLocation();

    String message();

    @Override
    int hashCode();

    @Override
    boolean equals(Object o);

    @Override
    String toString();

}
