/**
 * 
 */
package org.mutabilitydetector.checkers.settermethod;

/**
 * Implementations of this interface are supposed to find something within a
 * provided context. This context is out of the scope of this interface.
 * 
 * @param <T>
 *            type which is found by the implementation of this interface.
 * 
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 11.03.2013
 */
public interface Finder<T> {

    /**
     * @return an object of the found type or {@code null}. Have a look at
     *         documentation of implementations for specific behaviour.
     */
    T find();

}
