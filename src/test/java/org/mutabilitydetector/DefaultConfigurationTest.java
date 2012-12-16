package org.mutabilitydetector;

import static org.mutabilitydetector.unittesting.AllowedReason.provided;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;

import org.junit.Test;
import org.mutabilitydetector.unittesting.MutabilityMatchers;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class DefaultConfigurationTest {

    @Test
    public void isImmutable() throws Exception {
        assertInstancesOf(DefaultConfiguration.class, MutabilityMatchers.areImmutable(),
                          provided(ImmutableSet.class, ImmutableMap.class).isAlsoImmutable());
    }
    
}
