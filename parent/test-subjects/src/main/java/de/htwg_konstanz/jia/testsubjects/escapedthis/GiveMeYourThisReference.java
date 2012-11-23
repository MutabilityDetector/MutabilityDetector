/*
 *    Copyright (c) 2008-2011 Graham Allan
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package de.htwg_konstanz.jia.testsubjects.escapedthis;

import java.util.HashSet;

import de.htwg_konstanz.jia.testsubjects.escapedthis.unsafe.Unsafe.SetThisReferenceAsInstanceFieldOfOtherObject;

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
