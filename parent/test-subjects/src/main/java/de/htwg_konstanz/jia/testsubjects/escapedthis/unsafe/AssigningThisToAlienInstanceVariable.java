/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.escapedthis.unsafe;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 23.11.2012
 */
public final class AssigningThisToAlienInstanceVariable {

    public static final class SomeAlienClass {
        public AssigningThisToAlienInstanceVariable iAmVisible = null;
    }

    public AssigningThisToAlienInstanceVariable() {
        final SomeAlienClass alienClass = new SomeAlienClass();
        alienClass.iAmVisible = this;
    }

}
