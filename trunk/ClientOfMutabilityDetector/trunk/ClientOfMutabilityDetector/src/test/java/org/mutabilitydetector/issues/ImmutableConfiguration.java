package org.mutabilitydetector.issues;

import static org.mutabilitydetector.issues.AssumeCopiedIntoUnmodifiable.assuming;
import static org.mutabilitydetector.unittesting.AllowedReason.provided;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.Test;

@SuppressWarnings({ "unchecked", "rawtypes" })
public final class ImmutableConfiguration {

    private final Map<String, Object> propMap;

    public ImmutableConfiguration(AbstractConfiguration origConfiguration) {

        Map store = new HashMap();
        // we need the variables interpolated, which doesn't come at the getProperty level
        Configuration interpolated = origConfiguration.interpolatedConfiguration();
        final Iterator keyIter = interpolated.getKeys();
        while (keyIter.hasNext()) {
            String key = (String) keyIter.next();
            store.put(key, interpolated.getProperty(key));
        }
        this.propMap = Collections.unmodifiableMap(store);
    }
    
    public Object getSomeProperty(String key) {
        return propMap.get(key);
    }

    public static interface Configuration {
        Iterator getKeys();
        Object getProperty(String key);
    }

    public static abstract class AbstractConfiguration {
        public abstract Configuration interpolatedConfiguration();
    }
    
    public static class MutabilityTest {
        @Test
        public void immutableConfiguration_assumeAnyMapIsImmutable() throws Exception {
            assertInstancesOf(ImmutableConfiguration.class, 
                              areImmutable(),
                              provided(Map.class).isAlsoImmutable());
        }
        
        @Test
        public void immutableConfiguration_assumeACopyFromUnmodifiableCollection() throws Exception {
            assertInstancesOf(ImmutableConfiguration.class, 
                              areImmutable(),
                              assuming("propMap").hasCollectionsUnmodifiableTypeAssignedToIt());
        }

    }
}
