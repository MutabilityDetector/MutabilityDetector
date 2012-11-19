/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.constructors;

import java.util.Date;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 19.11.2012
 */
public final class DirectAssignmentOfMutableType {

    @SuppressWarnings("unused")
    private final Date date;

    public DirectAssignmentOfMutableType(final Date aDate) {
        date = aDate;
    }

}
