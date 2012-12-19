package org.mutabilitydetector;


import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

import org.mutabilitydetector.checkers.CheckerRunner.ExceptionPolicy;
import org.mutabilitydetector.locations.Dotted;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

@Immutable
public final class DefaultConfiguration implements Configuration {

    private final Set<AnalysisResult> hardcodedResults;
    private final Map<Dotted, AnalysisResult> resultsByClassname;
    private final ExceptionPolicy exceptionPolicy;

    public DefaultConfiguration(Set<AnalysisResult> predefinedResults, ExceptionPolicy exceptionPolicy) {
        this.exceptionPolicy = exceptionPolicy;
        this.hardcodedResults = ImmutableSet.<AnalysisResult>copyOf(predefinedResults);
        this.resultsByClassname = Maps.uniqueIndex(hardcodedResults, AnalysisResult.TO_DOTTED_CLASSNAME);
    }

    @Override
    public Map<Dotted, AnalysisResult> hardcodedResults() {
        return resultsByClassname;
    }
    
    @Override
    public ExceptionPolicy exceptionPolicy() {
        return exceptionPolicy;
    }
    
}