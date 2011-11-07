/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector.benchmarks.escapedthis;

import java.util.HashMap;


public class Unsafe {

    public static final class ThisPassedToOtherObject {
        public ThisPassedToOtherObject() {
            new GiveMeYourThisReference(this);
        }
    }
    
    public static final class ThisPassedToOtherObjectAsOneOfManyParameters {
        public ThisPassedToOtherObjectAsOneOfManyParameters() {
            new GiveMeYourThisReference(null, "hi there", 1, this, new HashMap<String, String>(), 1.0d);
        }
    }

    public static final class ThisPassedToPrivateStaticMethodWhichDoesNotPublishTheReference {
        public ThisPassedToPrivateStaticMethodWhichDoesNotPublishTheReference() {
            dontPublishThisReference(this);
        }

        private static void dontPublishThisReference(Object thisReference) {
            String toString = thisReference.toString();
            System.out.println(toString);
        }
    }

    public static final class ThisPassedToPrivateStaticMethodWhichDoesPublishReference {
        public ThisPassedToPrivateStaticMethodWhichDoesPublishReference() {
            publishThisReference(this);
        }

        private void publishThisReference(ThisPassedToPrivateStaticMethodWhichDoesPublishReference thisReference) {
            new GiveMeYourThisReference(thisReference);
        }
    }

    public static final class SetThisReferenceAsFieldOfOtherInstance {
        public SetThisReferenceAsFieldOfOtherInstance() {
            GiveMeYourThisReference.YOUR_THIS_REFERENCE = this;
        }
    }

    public static final class PassThisReferenceToStaticObject {
        public PassThisReferenceToStaticObject() {
            GiveMeYourThisReference.THIS_REFERENCE_MAP.add(this);
        }
    }

    public static class PassThisReferenceToStaticMethod {
        public PassThisReferenceToStaticMethod() {
            GiveMeYourThisReference.staticMethod(this);
        }
    }

    public static class SaveThisReferenceAsStaticFieldOfThisClass {
        public static SaveThisReferenceAsStaticFieldOfThisClass staticThis;

        public SaveThisReferenceAsStaticFieldOfThisClass() {
            staticThis = this;
        }
    }

    public static class PassThisReferenceToParameter {
        public PassThisReferenceToParameter(GiveMeYourThisReference otherObject) {
            otherObject.passReference(this);
        }
    }

    public static class PassInnerClassWithImplicitReferenceToThis {
        private String maySeeMeIncomplete;
        
        public PassInnerClassWithImplicitReferenceToThis(GiveMeYourThisReference passTo) {
            passTo.passReference(new InnerClass());
            maySeeMeIncomplete = "You might see the field with this string in it, you might not!";
        }

        private class InnerClass {
            InnerClass() {
                System.out.println(PassInnerClassWithImplicitReferenceToThis.this.maySeeMeIncomplete);
            }
        }
    }

    interface Anonymous {
    }

    public static class PassAnonymousInnerClassWithImplicitReferenceToThis {
        public PassAnonymousInnerClassWithImplicitReferenceToThis(GiveMeYourThisReference passTo) {
            passTo.passReference(new Anonymous() {
                // implicit this reference is here
            });
        }
    }
}
