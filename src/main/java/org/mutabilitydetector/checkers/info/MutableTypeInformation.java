package org.mutabilitydetector.checkers.info;

/*
 * #%L
 * MutabilityDetector
 * %%
 * Copyright (C) 2008 - 2014 Graham Allan
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.Configuration;
import org.mutabilitydetector.locations.Dotted;

import javax.annotation.concurrent.Immutable;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.newSetFromMap;

public final class MutableTypeInformation {

    private final AnalysisSession analysisSession;
    private final Configuration configuration;
    
    private final KnownCyclicReferences knownCyclicReferences = new KnownCyclicReferences();

    public MutableTypeInformation(AnalysisSession analysisSession, Configuration configuration) {
        this.analysisSession = analysisSession;
        this.configuration = configuration;
    }
    
    public ImmutableMultimap<String, CopyMethod> hardcodedCopyMethods() {
        return configuration.hardcodedCopyMethods();
    }

    public MutabilityLookup resultOf(Dotted ownerClass, Dotted fieldClass, AnalysisInProgress analysisInProgress) {
        Optional<AnalysisResult> alreadyComputedResult = existingResult(fieldClass);

        return(alreadyComputedResult.isPresent())
                ? MutabilityLookup.complete(alreadyComputedResult.get())
                : requestAnalysisIfNoCyclicReferenceDetected(ownerClass, fieldClass, analysisInProgress);
    }

    private MutabilityLookup requestAnalysisIfNoCyclicReferenceDetected(Dotted ownerClass, Dotted fieldClass, AnalysisInProgress analysisInProgress) {
        if (knownCyclicReferences.includes(ownerClass, fieldClass)) {
            CyclicReference cyclicReference = new CyclicReference(ownerClass, fieldClass);
            return MutabilityLookup.foundCyclicReference(cyclicReference);
        } else if (fieldClass.equals(ownerClass)) {
            CyclicReference cyclicReference = new CyclicReference(ownerClass, fieldClass);
            knownCyclicReferences.register(cyclicReference);
            return MutabilityLookup.foundCyclicReference(cyclicReference);
        } else if (analysisInProgress.contains(fieldClass)) {
            CyclicReference cyclicReference = new CyclicReference(analysisInProgress);
            knownCyclicReferences.register(cyclicReference);
            return MutabilityLookup.foundCyclicReference(cyclicReference);
        } else {
            AnalysisResult result = analysisSession.processTransitiveAnalysis(fieldClass, analysisInProgress.analysisStartedFor(ownerClass));
            return MutabilityLookup.complete(result);
        }
    }

    private Optional<AnalysisResult> existingResult(final Dotted fieldClass) {
        AnalysisResult hardcodedResult = configuration.hardcodedResults().get(fieldClass);
        if (hardcodedResult != null) {
            return Optional.of(hardcodedResult);
        }

        return Optional.fromNullable(analysisSession.resultsByClass().get(fieldClass));
    }
    
    private final static class KnownCyclicReferences {
        private final Set<CyclicReference> knownCyclicReferenceClass = newSetFromMap(new ConcurrentHashMap<CyclicReference, Boolean>());

        boolean includes(Dotted ownerClass, Dotted fieldClass) {
            return knownCyclicReferenceClass.contains(new CyclicReference(fieldClass, ownerClass));
        }

        void register(CyclicReference cyclicReference) {
            knownCyclicReferenceClass.add(cyclicReference);
        }

    }

    @Immutable
    public static final class CyclicReference {
        public final ImmutableList<Dotted> references;

        public CyclicReference(Dotted first, Dotted second) {
            this.references = ImmutableList.of(first, second);
        }

        public CyclicReference(AnalysisInProgress analysisInProgress) {
            this.references = analysisInProgress.inProgress;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CyclicReference that = (CyclicReference) o;
            return Objects.equal(ImmutableSet.copyOf(references), ImmutableSet.copyOf(that.references));
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(references);
        }

        @Override
        public String toString() {
            return "CyclicReference{" + Joiner.on(" -> ").join(references) + "}";
        }
    }
    
    @Immutable
    public static final class MutabilityLookup {
        public final AnalysisResult result;
        public final boolean foundCyclicReference;
        public final CyclicReference cyclicReference;
        
        private MutabilityLookup(AnalysisResult result, CyclicReference cyclicReference) {
            this.result = result;
            this.foundCyclicReference = result == null;
            this.cyclicReference = cyclicReference;
        }
        
        public static MutabilityLookup foundCyclicReference(CyclicReference cyclicReference) {
            return new MutabilityLookup(null, checkNotNull(cyclicReference));
        }
        
        public static MutabilityLookup complete(AnalysisResult result) {
            return new MutabilityLookup(checkNotNull(result), null);
        }
    }

}