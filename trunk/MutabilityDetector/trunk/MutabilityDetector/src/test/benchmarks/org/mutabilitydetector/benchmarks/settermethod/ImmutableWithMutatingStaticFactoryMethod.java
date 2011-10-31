/*
 * Mutability Detector
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * 
 * Further licensing information for this project can be found in
 * license/LICENSE.txt
 */

package org.mutabilitydetector.benchmarks.settermethod;

public class ImmutableWithMutatingStaticFactoryMethod {

    private int foo;

    public ImmutableWithMutatingStaticFactoryMethod(int foo) {
        this.foo = foo;
    }

    public int foo() {
        return this.foo;
    }

    public static ImmutableWithMutatingStaticFactoryMethod newAndMutatedInstance() {
        ImmutableWithMutatingStaticFactoryMethod toReturn = new ImmutableWithMutatingStaticFactoryMethod(0);
        toReturn.foo = 42; // mutating an instance after construction
        return toReturn;
    }

}
