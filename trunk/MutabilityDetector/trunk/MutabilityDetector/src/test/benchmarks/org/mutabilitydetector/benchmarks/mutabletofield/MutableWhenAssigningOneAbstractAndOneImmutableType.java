package org.mutabilitydetector.benchmarks.mutabletofield;

import org.mutabilitydetector.benchmarks.types.AbstractType;
import org.mutabilitydetector.benchmarks.types.InterfaceType;

@SuppressWarnings({ "unused", "hiding" })
public class MutableWhenAssigningOneAbstractAndOneImmutableType {
    private AbstractType abstractType;
    private InterfaceType interfaceType;

    public void assign(AbstractType abstractType, InterfaceType interfaceType) {
        this.abstractType = abstractType;
        this.interfaceType = interfaceType;
    }
}
