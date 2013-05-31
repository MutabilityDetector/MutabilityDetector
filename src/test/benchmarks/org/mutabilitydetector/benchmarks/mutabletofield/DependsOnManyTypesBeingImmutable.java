package org.mutabilitydetector.benchmarks.mutabletofield;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.mutabilitydetector.benchmarks.ImmutableExample;
import org.mutabilitydetector.benchmarks.types.AbstractType;
import org.mutabilitydetector.benchmarks.types.InterfaceType;

public final class DependsOnManyTypesBeingImmutable {

    public final ImmutableExample myImmutableField;
    public final Map<AbstractType, InterfaceType> mapWithRequiredImmutableTypes;
    
    public DependsOnManyTypesBeingImmutable(ImmutableExample myImmutableField, 
                                            AbstractType myAbstractClassField,
                                            Map<AbstractType, InterfaceType> someInterfaceTypes) {
        this.myImmutableField = myImmutableField;
        this.mapWithRequiredImmutableTypes = Collections.unmodifiableMap(new HashMap<AbstractType, InterfaceType>(someInterfaceTypes));
    }
    
}
