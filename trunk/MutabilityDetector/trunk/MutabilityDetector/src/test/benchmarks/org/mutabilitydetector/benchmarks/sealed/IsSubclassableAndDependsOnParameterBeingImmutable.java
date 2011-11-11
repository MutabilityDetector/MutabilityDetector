package org.mutabilitydetector.benchmarks.sealed;

import org.mutabilitydetector.benchmarks.ImmutableProvidedOtherClassIsImmutable.ThisHasToBeImmutable;

public class IsSubclassableAndDependsOnParameterBeingImmutable {

    @SuppressWarnings("unused")
    private final ThisHasToBeImmutable thisHasToBeImmutable;
    
    public IsSubclassableAndDependsOnParameterBeingImmutable(ThisHasToBeImmutable thisHasToBeImmutable) {
        this.thisHasToBeImmutable = thisHasToBeImmutable;

    }
    
}
