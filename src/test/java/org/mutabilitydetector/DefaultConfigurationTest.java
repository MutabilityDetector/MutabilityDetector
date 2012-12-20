package org.mutabilitydetector;

import static org.mutabilitydetector.checkers.CheckerRunner.ExceptionPolicy.FAIL_FAST;
import static org.mutabilitydetector.unittesting.AllowedReason.provided;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;

import java.util.Collections;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class DefaultConfigurationTest {

    @Test
    public void isImmutable() throws Exception {
        Configuration configuration = Configurations.from(Collections.<AnalysisResult>emptySet(), FAIL_FAST);
        assertInstancesOf(configuration.getClass(), 
                          areImmutable(),
                          provided(ImmutableSet.class, ImmutableMap.class).isAlsoImmutable());
    }
    
}
