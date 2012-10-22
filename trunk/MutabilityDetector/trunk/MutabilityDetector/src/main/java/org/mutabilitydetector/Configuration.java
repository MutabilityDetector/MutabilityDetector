package org.mutabilitydetector;

import static com.google.common.collect.Iterables.tryFind;
import static org.mutabilitydetector.AnalysisResult.definitelyImmutable;

import java.util.Collections;
import java.util.Set;

import org.mutabilitydetector.locations.Dotted;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;

public final class Configuration {

    public final Set<AnalysisResult> hardcodedResults;

    public Configuration(Set<AnalysisResult> predefinedResults) {
        this.hardcodedResults = ImmutableSet.<AnalysisResult>copyOf(predefinedResults);
    }
    
    public static final Configuration NO_CONFIGURATION = new Configuration(Collections.<AnalysisResult>emptySet());
    
    public static final Configuration JDK = new Configuration(
            new ImmutableSet.Builder<AnalysisResult>()
            .add(definitelyImmutable("java.lang.String"))
            .add(definitelyImmutable("java.lang.Integer"))
            .add(definitelyImmutable("java.lang.Class"))
            .add(definitelyImmutable("java.math.BigDecimal"))
            .add(definitelyImmutable("java.math.BigInteger"))
            .build()
    );
    
    public Optional<AnalysisResult> hardcodedResultFor(Dotted className) {
        return tryFind(hardcodedResults, isResultFor(className));
    }

    private Predicate<AnalysisResult> isResultFor(final Dotted className) {
        return new Predicate<AnalysisResult>() {
            @Override public boolean apply(AnalysisResult input) {
                return input.dottedClassName.equals(className.asString());
            }
        };
    }
    
}