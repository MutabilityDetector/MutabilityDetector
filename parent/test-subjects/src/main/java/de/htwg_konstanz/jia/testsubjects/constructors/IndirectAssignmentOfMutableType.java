/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.constructors;

import java.util.Date;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 21.11.2012
 */
public final class IndirectAssignmentOfMutableType {

    @SuppressWarnings("unused")
    private final Date date;

    public IndirectAssignmentOfMutableType(final Date aDate) {
        date = new Date(aDate.getTime());
    }

}
