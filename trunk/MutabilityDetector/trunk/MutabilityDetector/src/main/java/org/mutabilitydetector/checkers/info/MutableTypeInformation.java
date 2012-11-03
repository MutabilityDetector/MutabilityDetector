package org.mutabilitydetector.checkers.info;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.find;
import static java.util.Collections.newSetFromMap;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.concurrent.Immutable;

import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.Configuration;
import org.mutabilitydetector.locations.Dotted;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

public final class MutableTypeInformation {

    private final AnalysisSession analysisSession;
    private final Configuration configuration;
    
    private final KnownCircularReferences knownCircularReferences = new KnownCircularReferences();
    
    private final Set<Dotted> visited = newSetFromMap(new ConcurrentHashMap<Dotted, Boolean>());

    public MutableTypeInformation(AnalysisSession analysisSession, Configuration configuration) {
        this.analysisSession = analysisSession;
        this.configuration = configuration;
    }

    public MutabilityLookup resultOf(Dotted ownerClass, final Dotted fieldClass) {
        Optional<AnalysisResult> alreadyComputedResult = existingResult(fieldClass);
        
        if (alreadyComputedResult.isPresent()) {
            return MutabilityLookup.complete(alreadyComputedResult.get());
        }
        
        if (fieldClass.equals(ownerClass) || visited.contains(fieldClass)) {
            knownCircularReferences.register(ownerClass, fieldClass);
        }

        if (knownCircularReferences.includes(ownerClass, fieldClass)) {
            return MutabilityLookup.foundCyclicReference();
        }
        

        visited.add(ownerClass);
        
        AnalysisResult result = analysisSession.resultFor(fieldClass);
        
        visited.remove(ownerClass);
        
        return MutabilityLookup.complete(result);
    }

    private Optional<AnalysisResult> existingResult(final Dotted fieldClass) {
        AnalysisResult hardcodedResult = configuration.hardcodedResults().get(fieldClass);
        if (hardcodedResult != null) {
            return Optional.of(hardcodedResult);
        }
        
        return Optional.fromNullable(find(analysisSession.getResults(), AnalysisResult.forClass(fieldClass), null));
    }
    
    public int levelsDeep() {
        return visited.size();
    }
    
    private final static class KnownCircularReferences {
        private final Set<Dotted> knownCyclicReferenceClass = newSetFromMap(new ConcurrentHashMap<Dotted, Boolean>());

        public boolean includes(Dotted ownerClass, Dotted fieldClass) {
            return knownCyclicReferenceClass.contains(fieldClass) || knownCyclicReferenceClass.contains(ownerClass);
        }

        public void register(Dotted ownerClass, Dotted fieldClass) {
            knownCyclicReferenceClass.add(ownerClass);
            knownCyclicReferenceClass.add(fieldClass);
        }
        
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
            return new MutabilityLookup(checkNotNull(result));
        }
    }
    
    @Immutable
    static final class CircularReference {
        private final Dotted first;
        private final Dotted second;
        private final int hashCode;
        private final String toString;
        
        public CircularReference(Dotted first, Dotted second) {
            this.first = first;
            this.second = second;
            
            this.hashCode = Objects.hashCode(first, second);
            this.toString = "[" + first + "]->[" + second + "]";
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            
            CircularReference other = (CircularReference) obj;
            return (Objects.equal(first, other.first) && Objects.equal(second, other.second)) ||
                   (Objects.equal(first, other.second) && Objects.equal(second, other.first));
        }
        
        @Override
        public String toString() {
            return toString;
        }
    }

}