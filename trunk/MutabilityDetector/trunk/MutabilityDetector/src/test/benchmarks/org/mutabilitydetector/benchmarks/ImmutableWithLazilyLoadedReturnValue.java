/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector.benchmarks;

/**
 * This is similar to the pattern adopted by {@link java.lang.String#hashCode()}, where the value of the hash is lazily
 * computed and cached.
 */
public final class ImmutableWithLazilyLoadedReturnValue {

    public final int number;
    private int lazilyComputed = getAnInt();

    public ImmutableWithLazilyLoadedReturnValue(int number) {
        this.number = number;
    }

    public int getLazilyLoadedReturnValue() {
        if (lazilyComputed == 0) {
            lazilyComputed = 42;
        }
        return lazilyComputed;
    }

    static int getAnInt() {
        return -1;
    }
}
