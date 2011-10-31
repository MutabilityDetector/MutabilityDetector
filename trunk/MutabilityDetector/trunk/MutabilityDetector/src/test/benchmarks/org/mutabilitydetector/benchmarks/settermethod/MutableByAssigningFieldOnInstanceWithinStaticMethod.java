/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector.benchmarks.settermethod;

public class MutableByAssigningFieldOnInstanceWithinStaticMethod {

    private int foo;

    public MutableByAssigningFieldOnInstanceWithinStaticMethod(int foo) {
        this.foo = foo;
    }

    public int getFoo() {
        return foo;
    }

    public static void doSomething(MutableByAssigningFieldOnInstanceWithinStaticMethod bar) {
        bar.foo = 42;
    }

}
