/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.escapedthis.unsafe;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 22.11.2012
 */
public final class AddingThisToVisibleCollection {

    static final List<Object> CACHE = new ArrayList<Object>();

    public AddingThisToVisibleCollection() {
        CACHE.add(this);
    }

}
