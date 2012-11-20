/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.variables;

import static java.util.Calendar.DECEMBER;
import static java.util.Calendar.JANUARY;
import static java.util.Calendar.MARCH;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 15.11.2012
 */
public final class WithPrivateFinalDateArray {

    private final Date[] dates;

    public WithPrivateFinalDateArray() {
        dates = new Date[] {
            new GregorianCalendar(2012, MARCH, 19).getTime(),
            new GregorianCalendar(2012, DECEMBER, 21).getTime(),
            new GregorianCalendar(2013, JANUARY, 25).getTime()
        };
    }

    public Date getFirstDate() {
        return new Date(dates[0].getTime());
    }

    public Date getSecondDate() {
        return new Date(dates[1].getTime());
    }

    public Date getThirdDate() {
        return new Date(dates[2].getTime());
    }

}
