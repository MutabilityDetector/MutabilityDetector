/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.variables;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 15.11.2012
 */
public final class WithPrivateFinalIntArray {

    private final int[] numbers;

    public WithPrivateFinalIntArray() {
        numbers = new int[] { 19, 21, 25 };
    }

    public int getSum() {
        int result = 0;
        for (final int number : numbers) {
            result += number;
        }
        return result;
    }

}
