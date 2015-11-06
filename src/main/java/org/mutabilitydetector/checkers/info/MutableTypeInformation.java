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



import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.newSetFromMap;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.concurrent.Immutable;

import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.Configuration;
import org.mutabilitydetector.locations.Dotted;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMultimap;

public final class MutableTypeInformation {

    private final AnalysisSession analysisSession;
    private final Configuration configuration;
    
    private final KnownCircularReferences knownCircularReferences = new KnownCircularReferences();

    public MutableTypeInformation(AnalysisSession analysisSession, Configuration configuration) {
        this.analysisSession = analysisSession;
        this.configuration = configuration;
    }
    
    public ImmutableMultimap<String, CopyMethod> hardcodedCopyMethods() {
        return configuration.hardcodedCopyMethods();
    }

    public MutabilityLookup resultOf(Dotted ownerClass, Dotted fieldClass, AnalysisInProgress analysisInProgress) {
        Optional<AnalysisResult> alreadyComputedResult = existingResult(fieldClass);

        if (alreadyComputedResult.isPresent()) {
            return MutabilityLookup.complete(alreadyComputedResult.get());
        } else if (knownCircularReferences.includes(ownerClass, fieldClass)) {
            return MutabilityLookup.foundCyclicReference();
        } else if (fieldClass.equals(ownerClass) || analysisInProgress.contains(fieldClass)) {
            knownCircularReferences.register(ownerClass, fieldClass);
            return MutabilityLookup.foundCyclicReference();
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
    
    private final static class KnownCircularReferences {
        private final Set<Dotted> knownCyclicReferenceClass = newSetFromMap(new ConcurrentHashMap<Dotted, Boolean>());

        boolean includes(Dotted ownerClass, Dotted fieldClass) {
            return knownCyclicReferenceClass.contains(fieldClass) || knownCyclicReferenceClass.contains(ownerClass);
        }

        void register(Dotted ownerClass, Dotted fieldClass) {
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

}