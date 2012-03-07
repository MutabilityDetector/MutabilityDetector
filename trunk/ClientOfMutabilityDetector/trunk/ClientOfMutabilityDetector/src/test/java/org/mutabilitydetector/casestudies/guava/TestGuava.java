package org.mutabilitydetector.casestudies.guava;

import static org.mutabilitydetector.unittesting.AllowedReason.allowingForSubclassing;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class TestGuava {

    @Test
    public void com_google_common_collect_ImmutableList() throws Exception {
        assertInstancesOf(ImmutableList.class, areImmutable(), allowingForSubclassing());
    }

}
