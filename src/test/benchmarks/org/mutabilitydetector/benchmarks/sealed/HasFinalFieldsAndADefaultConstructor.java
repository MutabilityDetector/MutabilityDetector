package org.mutabilitydetector.benchmarks.sealed;

public class HasFinalFieldsAndADefaultConstructor {
    public final double finalDouble = 2.1d;
}

class HasNonFinalFieldsAndADefaultConstructor {
    
}

class WatchMeSubclassYou extends HasFinalFieldsAndADefaultConstructor {
    
}

class WatchMeSubclassYou2 extends HasNonFinalFieldsAndADefaultConstructor {
    
}
