/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector.benchmarks.sealed;

public class SealedImmutable {

    private final String field;

    /* default */SealedImmutable(String field) {
        this.field = field;
    }

    public String field() {
        return field;
    }
}
