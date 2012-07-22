package org.mutabilitydetector.checkers;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mutabilitydetector.locations.Dotted.dotted;

import org.junit.Test;

public class CollectionTypeWrappedInUnmodifiableIdiomCheckerTest {

    @Test
    public void onlyUnmodifiableTypesOfferedByCollectionsAreRecognised() throws Exception {
        CollectionTypeWrappedInUmodifiableIdiomChecker checker = new CollectionTypeWrappedInUmodifiableIdiomChecker();
        
        assertTrue(checker.isCollectionTypeWhichCanBeWrappedInUmodifiableVersion(dotted("java.util.Collection")));
        assertTrue(checker.isCollectionTypeWhichCanBeWrappedInUmodifiableVersion(dotted("java.util.Set")));
        assertTrue(checker.isCollectionTypeWhichCanBeWrappedInUmodifiableVersion(dotted("java.util.SortedSet")));
        assertTrue(checker.isCollectionTypeWhichCanBeWrappedInUmodifiableVersion(dotted("java.util.List")));
        assertTrue(checker.isCollectionTypeWhichCanBeWrappedInUmodifiableVersion(dotted("java.util.Map")));
        assertTrue(checker.isCollectionTypeWhichCanBeWrappedInUmodifiableVersion(dotted("java.util.SortedMap")));
        
        assertFalse(checker.isCollectionTypeWhichCanBeWrappedInUmodifiableVersion(dotted("java.lang.Object")));
        assertFalse(checker.isCollectionTypeWhichCanBeWrappedInUmodifiableVersion(dotted("some.other.codebase.specific.Thingy")));
    }
    
}
