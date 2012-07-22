package org.mutabilitydetector.checkers;

import org.mutabilitydetector.locations.Dotted;

import com.google.common.collect.ImmutableSet;

class CollectionTypeWrappedInUmodifiableIdiomChecker {
    
    private static final ImmutableSet<String> CAN_BE_WRAPPED = ImmutableSet.of("java.util.Collection",
                                                                               "java.util.Set",
                                                                               "java.util.SortedSet",
                                                                               "java.util.List",
                                                                               "java.util.Map",
                                                                               "java.util.SortedMap");
    
    public boolean isCollectionTypeWhichCanBeWrappedInUmodifiableVersion(Dotted className) {
        return CAN_BE_WRAPPED.contains(className.asString());
    }
}