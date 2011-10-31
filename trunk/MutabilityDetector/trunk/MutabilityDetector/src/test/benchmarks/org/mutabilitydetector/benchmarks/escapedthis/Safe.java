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

    public static class SaveThisReferenceToPrivateInstanceField {
        private SaveThisReferenceToPrivateInstanceField thisReference;

        public SaveThisReferenceToPrivateInstanceField() {
            thisReference = this;
        }
    }

    public static class PassThisReferenceToClassWhichDoesNotPublishIt {
        public PassThisReferenceToClassWhichDoesNotPublishIt() {
            new JustAssignTheReference(this);
        }
    }

    private static class JustAssignTheReference {
        private final Object reference;

        public JustAssignTheReference(Object reference) {
            this.reference = reference;
        }
    }
}
