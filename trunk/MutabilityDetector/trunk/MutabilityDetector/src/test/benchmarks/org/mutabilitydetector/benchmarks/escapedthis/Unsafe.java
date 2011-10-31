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

public class Unsafe {

    public static final class ThisPassedToOtherObject {
        public ThisPassedToOtherObject() {
            new GiveMeYourThisReference(this);
        }
    }

    public static class ThisPassedToPrivateStaticMethodWhichDoesNotPublishTheReference {
        public ThisPassedToPrivateStaticMethodWhichDoesNotPublishTheReference() {
            dontPublishThisReference(this);
        }

        private static void dontPublishThisReference(Object thisReference) {
            String toString = thisReference.toString();
            System.out.println(toString);
        }
    }

    public static class ThisPassedToPrivateStaticMethodWhichDoesPublishReference {
        public ThisPassedToPrivateStaticMethodWhichDoesPublishReference() {
            publishThisReference(this);
        }

        private void publishThisReference(ThisPassedToPrivateStaticMethodWhichDoesPublishReference thisReference) {
            new GiveMeYourThisReference(thisReference);
        }
    }

    public static class SetThisReferenceAsFieldOfOtherInstance {
        public SetThisReferenceAsFieldOfOtherInstance() {
            GiveMeYourThisReference.YOUR_THIS_REFERENCE = this;
        }
    }

    public static class PassThisReferenceToStaticObject {
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
        public PassInnerClassWithImplicitReferenceToThis(GiveMeYourThisReference passTo) {
            passTo.passReference(new InnerClass());
        }

        private class InnerClass {
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
