/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.methods;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 22.11.2012
 */
public final class WithObviousSideEffect {

    private int counter = 0;

    public synchronized void increaseCounterByOne() {
        counter++;
    }

    public synchronized int getCounter() {
        return counter;
    }

}
