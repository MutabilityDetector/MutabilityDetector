package com.google.common.collect;

import static org.mutabilitydetector.unittesting.AllowedReason.allowingForSubclassing;
import static org.mutabilitydetector.unittesting.AllowedReason.provided;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;

import net.ttsui.junit.rules.pending.PendingImplementation;
import net.ttsui.junit.rules.pending.PendingRule;

import org.junit.Rule;
import org.junit.Test;

public class TestGuavasImmutableCollections {

    @Test
    public void com_google_common_collect_EmptyImmutableList() throws Exception {
        assertInstancesOf(EmptyImmutableList.class, areImmutable(), allowingForSubclassing());
    }
    
    @Test
    public void com_google_common_collect_ImmutableSortedAsList() throws Exception {
        assertInstancesOf(ImmutableSortedAsList.class, areImmutable(),
                          provided(ImmutableSortedSet.class).isAlsoImmutable(),
                          provided(ImmutableList.class).isAlsoImmutable());
    }
    
    
    @Rule public PendingRule rule = new PendingRule();
    
    @PendingImplementation
    @Test
    public void com_google_common_collect_RegularImmutableList() throws Exception {
        assertInstancesOf(RegularImmutableList.class, areImmutable(), allowingForSubclassing());
    }

    @Test
    public void com_google_common_collect_ImmutableAsList() throws Exception {
        assertInstancesOf(ImmutableAsList.class, areImmutable(),
                          provided(ImmutableCollection.class).isAlsoImmutable());
    }
    
    @Test
    public void com_google_common_collect_SingletonImmutableList() throws Exception {
        assertInstancesOf(SingletonImmutableList.class, areImmutable(), provided(Object.class).isAlsoImmutable());
    }
    
    @Test
    public void com_google_common_collect_TransformedImmutableList() throws Exception {
        assertInstancesOf(TransformedImmutableList.class, areImmutable(),
                          allowingForSubclassing(),
                          provided(ImmutableList.class).isAlsoImmutable());
    }
}
