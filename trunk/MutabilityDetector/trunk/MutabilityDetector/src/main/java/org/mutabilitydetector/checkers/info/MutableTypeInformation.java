package org.mutabilitydetector.checkers.info;

import static java.util.Collections.newSetFromMap;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.concurrent.Immutable;

import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.Configuration;
import org.mutabilitydetector.locations.Dotted;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public final class MutableTypeInformation {

    private final AnalysisSession analysisSession;
    private final Configuration configuration;
    private final Set<Dotted> inProgressAnalysis = newSetFromMap(new ConcurrentHashMap<Dotted, Boolean>());

    public MutableTypeInformation(AnalysisSession analysisSession, Configuration configuration) {
        this.analysisSession = analysisSession;
        this.configuration = configuration;
    }

    public MutabilityLookup resultOf(final Dotted fieldClass, Dotted ownerClass) {
        AnalysisResult hardcodedResult = configuration.hardcodedResults().get(fieldClass);
        if (hardcodedResult != null) {
            return MutabilityLookup.complete(hardcodedResult);
        }
        
        AnalysisResult alreadyComputedResult = existingResult(fieldClass);
        
        if (alreadyComputedResult != null) {
            return MutabilityLookup.complete(alreadyComputedResult);
        }
        
        if (fieldClass.equals(ownerClass)) {
            inProgressAnalysis.remove(ownerClass);
            return MutabilityLookup.foundCyclicReference();
        }
        
        if (inProgressAnalysis.contains(ownerClass)) {
            inProgressAnalysis.remove(ownerClass);
            return MutabilityLookup.foundCyclicReference();
        }
        
        inProgressAnalysis.add(ownerClass);
        
        AnalysisResult result = analysisSession.resultFor(fieldClass);
        
        if (result != null) {
            inProgressAnalysis.remove(ownerClass);
            return MutabilityLookup.complete(result);
        }
        return MutabilityLookup.foundCyclicReference();
    }

    private AnalysisResult existingResult(final Dotted fieldClass) {
        return Iterables.find(analysisSession.getResults(), new Predicate<AnalysisResult>() {
            @Override public boolean apply(AnalysisResult input) {
                return input.dottedClassName.equals(fieldClass.asString());
            }}, null);
    }
    
    @Immutable
    public static final class MutabilityLookup {
        public final AnalysisResult result;
        public final boolean foundCyclicReference;
        
        private MutabilityLookup(AnalysisResult result) {
            this.result = result;
            this.foundCyclicReference = result == null;
        }
        
        public static MutabilityLookup foundCyclicReference() {
            return new MutabilityLookup(null);
        }
        
        public static MutabilityLookup complete(AnalysisResult result) {
            return new MutabilityLookup(result);
        }
    }
}