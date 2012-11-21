package de.htwg_konstanz.jia.testsubjects.variables;

import java.util.Date;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 21.11.2012
 */
public final class IndirectReturnOfMutableType {

    private final Date date = new Date(System.currentTimeMillis());

    public Date getDate() {
        return new Date(date.getTime());
    }

}
