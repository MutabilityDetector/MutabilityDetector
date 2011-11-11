/*
 * Mutability Detector
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * 
 * Further licensing information for this project can be found in
 * license/LICENSE.txt
 */

package org.mutabilitydetector.benchmarks.escapedthis;

import java.util.HashSet;

import org.mutabilitydetector.benchmarks.escapedthis.Unsafe.SetThisReferenceAsInstanceFieldOfOtherObject;

@SuppressWarnings("unused")
public class GiveMeYourThisReference {

    private final String obtainedStringField;
    public SetThisReferenceAsInstanceFieldOfOtherObject instanceOfYourThis;

    /**
     * In a multithreaded environment, with a lack of synchronisation, if a 'this' reference is passed within a
     * constructor, the reference could point to an object in an incomplete state. Thus
     * {@link #GiveMeYourThisReference(Object)} could see the object changing, thus the object would not be immutable.
     */
    public GiveMeYourThisReference(Object yourThisReference) {
        obtainedStringField = yourThisReference.toString();
    }

    public GiveMeYourThisReference(Object first, String second, long third, Object theThisReference, Object andAnother, double d) {
        this(theThisReference);
    }
    
    // As first parameter
    public GiveMeYourThisReference(Object theThisReference, Object first, String second, long third, double d, Object andAnother) {
        this(theThisReference);
    }

    // As last parameter
    public GiveMeYourThisReference(Object first, String second, long third, double d, Object andAnother, Object theThisReference) {
        this(theThisReference);
    }
    
    public String stringField() {
        return obtainedStringField;
    }

    public void passReference(Object thisReference) {
    }

    /**
     * Publishing your this reference to a static object is an unsafe publication.
     */
    public static Object YOUR_THIS_REFERENCE;

    public static final HashSet<Object> THIS_REFERENCE_MAP = new HashSet<Object>();

    public static void staticMethod(Object passThisReferenceToStaticMethod) {
    }

}
