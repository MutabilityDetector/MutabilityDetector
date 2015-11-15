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


import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMultimap;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.Configuration;
import org.mutabilitydetector.checkers.info.CyclicReferences.CyclicReference;
import org.mutabilitydetector.locations.Dotted;

import javax.annotation.concurrent.Immutable;

import static com.google.common.base.Preconditions.checkNotNull;

public final class MutableTypeInformation {

    private final AnalysisSession analysisSession;
    private final Configuration configuration;
    private final CyclicReferences cyclicReferences;

    public MutableTypeInformation(AnalysisSession analysisSession,
                                  Configuration configuration,
                                  CyclicReferences cyclicReferences) {
        this.analysisSession = analysisSession;
        this.configuration = configuration;
        this.cyclicReferences = cyclicReferences;
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
        Optional<CyclicReference> cyclicReference = cyclicReferences.detectedBetween(ownerClass, fieldClass, analysisInProgress);
        if (cyclicReference.isPresent()) {
            return MutabilityLookup.foundCyclicReference(cyclicReference.get());
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