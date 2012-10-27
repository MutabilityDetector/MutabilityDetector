package org.mutabilitydetector;

import static org.mutabilitydetector.AnalysisResult.definitelyImmutable;
import static org.mutabilitydetector.locations.Dotted.dotted;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.mutabilitydetector.locations.Dotted;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

public final class DefaultConfiguration implements Configuration {

    public final Set<AnalysisResult> hardcodedResults;
    public final Map<Dotted, AnalysisResult> resultsByClassname;

    public DefaultConfiguration(Set<AnalysisResult> predefinedResults) {
        this.hardcodedResults = ImmutableSet.<AnalysisResult>copyOf(predefinedResults);
        this.resultsByClassname = Maps.uniqueIndex(hardcodedResults, BY_CLASS_NAME);
    }

    private Function<AnalysisResult, Dotted> BY_CLASS_NAME = new Function<AnalysisResult, Dotted>() {
        @Override public Dotted apply(AnalysisResult input) {
            return dotted(input.dottedClassName);
        }
    };
    
    public static final Configuration NO_CONFIGURATION = new DefaultConfiguration(Collections.<AnalysisResult>emptySet());
    
    public static final Configuration JDK = new DefaultConfiguration(
            new ImmutableSet.Builder<AnalysisResult>()
            .add(definitelyImmutable("java.lang.String"))
            .add(definitelyImmutable("java.lang.Integer"))
            .add(definitelyImmutable("java.lang.Class"))
            .add(definitelyImmutable("java.math.BigDecimal"))
            .add(definitelyImmutable("java.math.BigInteger"))
            .build()
    );


    @Override
    public Map<Dotted, AnalysisResult> hardcodedResults() {
        return resultsByClassname;
    }
    
}