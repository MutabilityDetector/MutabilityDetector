package org.mutabilitydetector;

import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Sets.newHashSet;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

import org.mutabilitydetector.CheckerRunner.ExceptionPolicy;

import com.google.common.base.Equivalence;
import com.google.common.base.Equivalence.Wrapper;
import com.google.common.base.Equivalences;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

@NotThreadSafe
public abstract class ConfigurationBuilder {

    private static final ExceptionPolicy DEFAULT_EXCEPTION_POLICY = ExceptionPolicy.FAIL_FAST;

    public abstract void configure();
    
    public final Configuration build() {
        configure();
        return new DefaultConfiguration(hardcodedResults.build(), exceptionPolicy);
    }
    
    private ImmutableSet.Builder<AnalysisResult> hardcodedResults = ImmutableSet.builder();
    private ExceptionPolicy exceptionPolicy = DEFAULT_EXCEPTION_POLICY;
    
    protected final void hardcodeResult(AnalysisResult result) {
        hardcodedResults.add(result);
    }

    protected final void hardcodeResults(Iterable<AnalysisResult> result) {
        hardcodedResults.addAll(result);
    }

    protected final void hardcodeResults(AnalysisResult... result) {
        hardcodeResults(Arrays.asList(result));
    }
    
    protected final void hardcodeAsDefinitelyImmutable(Class<?> immutableClass) {
        hardcodeResult(AnalysisResult.definitelyImmutable(immutableClass.getName()));
    }
    
    protected final Set<AnalysisResult> getCurrentlyOverriddenResults() {
        return hardcodedResults.build();
    }

    protected void mergeHardcodedResultsFrom(Configuration existing) {
        Set<Wrapper<AnalysisResult>> union = 
                Sets.union(newHashSet(transform(hardcodedResults.build(), TO_CLASSNAME_EQUIVALENCE_WRAPPER)), 
                           copyOf(transform(existing.hardcodedResults().values(), TO_CLASSNAME_EQUIVALENCE_WRAPPER)));
        
        Set<AnalysisResult> result = copyOf(transform(union, UNWRAP));
        
        hardcodedResults = ImmutableSet.<AnalysisResult>builder().addAll(result);
    }

    private static final Equivalence<AnalysisResult> CLASSNAME_EQUIVALENCE = Equivalences.equals().onResultOf(AnalysisResult.TO_CLASSNAME);

    private static final Function<AnalysisResult, Wrapper<AnalysisResult>> TO_CLASSNAME_EQUIVALENCE_WRAPPER = 
        new Function<AnalysisResult, Wrapper<AnalysisResult>>() {
            @Override public Wrapper<AnalysisResult> apply(AnalysisResult input) {
                return CLASSNAME_EQUIVALENCE.wrap(input);
            }
    };
    
    private Function<Wrapper<AnalysisResult>, AnalysisResult> UNWRAP = new Function<Wrapper<AnalysisResult>, AnalysisResult>() {
        @Override public AnalysisResult apply(Wrapper<AnalysisResult> input) { return input.get(); }
    };
    
    protected final void setExceptionPolicy(ExceptionPolicy exceptionPolicy) {
        this.exceptionPolicy = exceptionPolicy;
    }
    
    public static final Configuration JDK = new ConfigurationBuilder() {
        @Override
        public void configure() {
            hardcodeAsDefinitelyImmutable(String.class);
            hardcodeAsDefinitelyImmutable(Integer.class);
            hardcodeAsDefinitelyImmutable(Class.class);
            hardcodeAsDefinitelyImmutable(BigDecimal.class);
            hardcodeAsDefinitelyImmutable(BigInteger.class);
        }
    }.build();
    
    public static final Configuration NO_CONFIGURATION = new ConfigurationBuilder() {
        @Override public void configure() { }
    }.build();
    

}
