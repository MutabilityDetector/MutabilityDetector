package org.mutabilitydetector;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Set;

import org.mutabilitydetector.CheckerRunner.ExceptionPolicy;

import com.google.common.collect.ImmutableSet;

public abstract class ConfigurationBuilder {

    private static final ExceptionPolicy DEFAULT_EXCEPTION_POLICY = ExceptionPolicy.FAIL_FAST;

    public abstract void configure();
    
    public final Configuration build() {
        configure();
        return new DefaultConfiguration(overriddenResults.build(), exceptionPolicy);
    }
    
    private final ImmutableSet.Builder<AnalysisResult> overriddenResults = ImmutableSet.builder();
    private ExceptionPolicy exceptionPolicy = DEFAULT_EXCEPTION_POLICY;
    
    protected final void overrideResult(AnalysisResult result) {
        overriddenResults.add(result);
    }

    protected final void overrideResults(Iterable<AnalysisResult> result) {
        overriddenResults.addAll(result);
    }

    protected final void overrideResults(AnalysisResult... result) {
        overrideResults(Arrays.asList(result));
    }

    protected final void overrideAsDefinitelyImmutable(Class<?> immutableClass) {
        overrideResult(AnalysisResult.definitelyImmutable(immutableClass.getName()));
    }
    
    protected final Set<AnalysisResult> getCurrentlyOverriddenResults() {
        return overriddenResults.build();
    }
    
    protected final void setExceptionPolicy(ExceptionPolicy exceptionPolicy) {
        this.exceptionPolicy = exceptionPolicy;
    }
    
    
    public static final Configuration JDK = new ConfigurationBuilder() {
        @Override
        public void configure() {
            overrideAsDefinitelyImmutable(String.class);
            overrideAsDefinitelyImmutable(Integer.class);
            overrideAsDefinitelyImmutable(Class.class);
            overrideAsDefinitelyImmutable(BigDecimal.class);
            overrideAsDefinitelyImmutable(BigInteger.class);
        }
    }.build();
    
    public static final Configuration NO_CONFIGURATION = new ConfigurationBuilder() {
        @Override public void configure() { }
    }.build();
    

}
