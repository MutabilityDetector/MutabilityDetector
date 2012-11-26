/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.escapedthis.safe;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 26.11.2012
 */
public class InvokeFinalInstanceMethod {

    public InvokeFinalInstanceMethod() {
        doNothing();
    }

    protected final void doNothing() {
        System.out.println("Nothing");
    }

}
