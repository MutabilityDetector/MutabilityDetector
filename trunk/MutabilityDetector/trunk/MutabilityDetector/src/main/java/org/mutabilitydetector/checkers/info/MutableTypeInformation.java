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
    private Configuration configuration;
    private final List<Dotted> inProgressAnalysis = newArrayList();

    public MutableTypeInformation(AnalysisSession analysisSession, Configuration configuration) {
        this.analysisSession = analysisSession;
        this.configuration = configuration;
    }

    public RequestedAnalysis resultOf(final Dotted dotted, Dotted askedOnBehalfOf) {
        AnalysisResult hardcodedResult = configuration.hardcodedResults().get(dotted);
        if (hardcodedResult != null) {
            return RequestedAnalysis.complete(hardcodedResult);
        }
        
        AnalysisResult alreadyComputedResult = Iterables.find(analysisSession.getResults(), new Predicate<AnalysisResult>() {
            @Override
            public boolean apply(AnalysisResult input) {
                return input.dottedClassName.equals(dotted.asString());
            }
            
        }, null);
        if (alreadyComputedResult != null) {
            return RequestedAnalysis.complete(alreadyComputedResult);
        }
        
        if (dotted.equals(askedOnBehalfOf)) {
            removeInProgressAnalysisOf(askedOnBehalfOf);
            return RequestedAnalysis.incomplete();
        }
        
        if (isAnalysisInProgress(askedOnBehalfOf)) {
            removeInProgressAnalysisOf(askedOnBehalfOf);
            return RequestedAnalysis.incomplete();
        }
        
        addInProgressAnalysisOf(askedOnBehalfOf);
        
        AnalysisResult result = analysisSession.resultFor(dotted);
        
        if (result != null) {
            removeInProgressAnalysisOf(askedOnBehalfOf);
            return RequestedAnalysis.complete(result);
        }
        return RequestedAnalysis.incomplete();
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
    public static final class RequestedAnalysis {
        public final AnalysisResult result;
        public final boolean analysisComplete;
        
        private RequestedAnalysis(AnalysisResult result) {
            this.result = result;
            this.analysisComplete = result != null;
        }
        
        public static RequestedAnalysis incomplete() {
            return new RequestedAnalysis(null);
        }
        
        public static RequestedAnalysis complete(AnalysisResult result) {
            return new RequestedAnalysis(result);
        }
    }
}