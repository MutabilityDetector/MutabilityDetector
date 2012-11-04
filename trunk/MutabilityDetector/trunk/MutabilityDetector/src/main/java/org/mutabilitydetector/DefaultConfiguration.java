package org.mutabilitydetector;

import static org.mutabilitydetector.locations.Dotted.dotted;

import java.util.Map;
import java.util.Set;

import org.mutabilitydetector.CheckerRunner.ExceptionPolicy;
import org.mutabilitydetector.locations.Dotted;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

public final class DefaultConfiguration implements Configuration {

    private final Set<AnalysisResult> hardcodedResults;
    private final Map<Dotted, AnalysisResult> resultsByClassname;
    private final ExceptionPolicy exceptionPolicy;

    public DefaultConfiguration(Set<AnalysisResult> predefinedResults, ExceptionPolicy exceptionPolicy) {
        this.exceptionPolicy = exceptionPolicy;
        this.hardcodedResults = ImmutableSet.<AnalysisResult>copyOf(predefinedResults);
        this.resultsByClassname = Maps.uniqueIndex(hardcodedResults, BY_CLASS_NAME);
    }

    private Function<AnalysisResult, Dotted> BY_CLASS_NAME = new Function<AnalysisResult, Dotted>() {
        @Override public Dotted apply(AnalysisResult input) {
            return dotted(input.dottedClassName);
        }
    };
    
    @Override
    public Map<Dotted, AnalysisResult> hardcodedResults() {
        return resultsByClassname;
    }
    
    @Override
    public ExceptionPolicy exceptionPolicy() {
        return exceptionPolicy;
    }
    
}