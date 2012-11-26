/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.escapedthis.unsafe;

import de.htwg_konstanz.jia.testsubjects.escapedthis.GiveMeYourThisReference;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 26.11.2012
 */
public final class AliasesThisReferenceInInstanceMethod {

    public AliasesThisReferenceInInstanceMethod() {
        publishThisReference();
    }

    private void publishThisReference() {
        final AliasesThisReferenceInInstanceMethod thisReference = this;
        new GiveMeYourThisReference(thisReference);
    }

}
