package org.mutabilitydetector.benchmarks.mutabletofield.generic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class HasFieldUsingGenericTypeOfClass<MY_TYPE> {

    public final List<MY_TYPE> listOfT;

    public HasFieldUsingGenericTypeOfClass(List<MY_TYPE> listOfT) {
        this.listOfT = Collections.unmodifiableList(new ArrayList<MY_TYPE>(listOfT));
    }
}
