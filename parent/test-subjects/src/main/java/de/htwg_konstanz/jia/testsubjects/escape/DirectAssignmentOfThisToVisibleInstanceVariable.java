/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.escape;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 20.11.2012
 */
public final class DirectAssignmentOfThisToVisibleInstanceVariable {

    public final DirectAssignmentOfThisToVisibleInstanceVariable obj;

    public DirectAssignmentOfThisToVisibleInstanceVariable() {
        obj = this;
    }

}
