package org.mutabilitydetector.checkers.info;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

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
    private final List<Dotted> inProgressAnalysis = newArrayList();

    public MutableTypeInformation(AnalysisSession analysisSession, Configuration configuration) {
        this.analysisSession = analysisSession;
        this.configuration = configuration;
    }

    public MutabilityLookup resultOf(final Dotted dotted, Dotted askedOnBehalfOf) {
        AnalysisResult hardcodedResult = configuration.hardcodedResults().get(dotted);
        if (hardcodedResult != null) {
            return MutabilityLookup.complete(hardcodedResult);
        }
        
        AnalysisResult alreadyComputedResult = Iterables.find(analysisSession.getResults(), new Predicate<AnalysisResult>() {
            @Override
            public boolean apply(AnalysisResult input) {
                return input.dottedClassName.equals(dotted.asString());
            }
            
        }, null);
        
        if (alreadyComputedResult != null) {
            return MutabilityLookup.complete(alreadyComputedResult);
        }
        
        if (dotted.equals(askedOnBehalfOf)) {
            removeInProgressAnalysisOf(askedOnBehalfOf);
            return MutabilityLookup.foundCyclicReference();
        }
        
        if (isAnalysisInProgress(askedOnBehalfOf)) {
            removeInProgressAnalysisOf(askedOnBehalfOf);
            return MutabilityLookup.foundCyclicReference();
        }
        
        addInProgressAnalysisOf(askedOnBehalfOf);
        
        AnalysisResult result = analysisSession.resultFor(dotted);
        
        if (result != null) {
            removeInProgressAnalysisOf(askedOnBehalfOf);
            return MutabilityLookup.complete(result);
        }
        return MutabilityLookup.foundCyclicReference();
    }
    
    private boolean isAnalysisInProgress(Dotted className) {
        return inProgressAnalysis.contains(className);
    }

    private boolean addInProgressAnalysisOf(Dotted className) {
        return inProgressAnalysis.add(className);
    }

    private boolean removeInProgressAnalysisOf(Dotted className) {
        return inProgressAnalysis.remove(className);
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