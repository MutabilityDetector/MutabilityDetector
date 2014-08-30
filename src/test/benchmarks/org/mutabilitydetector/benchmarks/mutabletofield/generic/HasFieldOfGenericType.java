package org.mutabilitydetector.benchmarks.mutabletofield.generic;

import java.util.ArrayList;

import org.mutabilitydetector.benchmarks.mutabletofield.generic.HasFieldOfGenericType.MyInterface;

public final class HasFieldOfGenericType<T extends Enum<T> & MyInterface, N> extends ArrayList<T> {

    public final T fieldOfT;
    public final N fieldOfN;
    public final String string = "Hi";

    public HasFieldOfGenericType(T t, N n) {
        this.fieldOfT = t;
        this.fieldOfN = n;
    }

    static interface MyInterface {}

    static enum MyEnum implements MyInterface {}
}
