/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.escape;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 20.11.2012
 */
public final class DirectAssignmentOfThisToVisibleClassVariable {

    public static DirectAssignmentOfThisToVisibleClassVariable instance;

    public DirectAssignmentOfThisToVisibleClassVariable() {
        instance = this;
    }

}
