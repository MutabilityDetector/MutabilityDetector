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

package org.mutabilitydetector.benchmarks.escapedthis;



public class Unsafe {

    public static final class ThisPassedToPrivateMethodWhichDoesPublishReference {
        public ThisPassedToPrivateMethodWhichDoesPublishReference() {
            publishThisReference(this);
        }

        private void publishThisReference(ThisPassedToPrivateMethodWhichDoesPublishReference thisReference) {
            new GiveMeYourThisReference(thisReference);
        }
    }

    public static final class SetThisReferenceAsStaticFieldOfOtherClass {
        public SetThisReferenceAsStaticFieldOfOtherClass() {
            GiveMeYourThisReference.YOUR_THIS_REFERENCE = this;
        }
    }

    public static final class SetThisReferenceAsInstanceFieldOfOtherObject {
        public SetThisReferenceAsInstanceFieldOfOtherObject(GiveMeYourThisReference giveMeIt) {
            giveMeIt.instanceOfYourThis = this;
        }
    }
    
    public static final class AliasesThisReferenceBeforeLettingItEscape {
        public AliasesThisReferenceBeforeLettingItEscape() {
            Object alias = this;
            new GiveMeYourThisReference(alias);
        }
    }

    
    public static final class PassThisReferenceToStaticObject {
        public PassThisReferenceToStaticObject() {
            GiveMeYourThisReference.THIS_REFERENCE_MAP.add(this);
        }
    }
    
    public static final class SaveThisReferenceAsInstanceFieldOfThisClass {
        public final SaveThisReferenceAsInstanceFieldOfThisClass instanceThis;

        public SaveThisReferenceAsInstanceFieldOfThisClass() {
            instanceThis = this;
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
        private final String maySeeMeIncomplete;
        
        public PassInnerClassWithImplicitReferenceToThis(GiveMeYourThisReference passTo) {
            Object somethingElse = new Object();
            passTo.passReference(new InnerClass(somethingElse));
            maySeeMeIncomplete = "You might see the field with this string in it, you might not!";
        }

        private class InnerClass {
            InnerClass(Object somethingElse) {
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
