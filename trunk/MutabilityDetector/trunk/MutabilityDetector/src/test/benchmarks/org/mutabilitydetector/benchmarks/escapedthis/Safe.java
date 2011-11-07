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

@SuppressWarnings("unused")
public class Safe {

    public static final class SaveThisReferenceToPrivateInstanceField {
        private final SaveThisReferenceToPrivateInstanceField thisReference;

        public SaveThisReferenceToPrivateInstanceField() {
            thisReference = this;
        }
    }

    public static final class PassThisReferenceToClassWhichDoesNotPublishIt {
        public PassThisReferenceToClassWhichDoesNotPublishIt() {
            new JustAssignTheReference(this);
        }
    }

    private static final class JustAssignTheReference {
        private final Object reference;

        public JustAssignTheReference(Object reference) {
            this.reference = reference;
        }
    }
    
    public static final class PassesThisReferenceAfterConstruction {
        private final int passed = 1;
        
        public void nowYouCanEscape() {
            new GiveMeYourThisReference(this);
        }
    }
    
}
