package org.mutabilitydetector;

import java.util.Arrays;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

public abstract class ConfigurationBuilder {

    public abstract void configure();
    
    public final Configuration build() {
        configure();
        return new DefaultConfiguration(overriddenResults.build());
    }
    
    private final ImmutableSet.Builder<AnalysisResult> overriddenResults = ImmutableSet.builder();
    
    
    final void overrideResult(AnalysisResult result) {
        overriddenResults.add(result);
    }

    final void overrideResults(Iterable<AnalysisResult> result) {
        overriddenResults.addAll(result);
    }

    final void overrideResults(AnalysisResult... result) {
        overrideResults(Arrays.asList(result));
    }

    final void overrideAsDefinitelyImmutable(Class<?> immutableClass) {
        overrideResult(AnalysisResult.definitelyImmutable(immutableClass.getName()));
    }
    
    final Set<AnalysisResult> getCurrentlyOverriddenResults() {
        return overriddenResults.build();
    }

}
