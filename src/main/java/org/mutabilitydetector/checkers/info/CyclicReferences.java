package org.mutabilitydetector.checkers.info;

/*
 * #%L
 * MutabilityDetector
 * %%
 * Copyright (C) 2008 - 2015 Graham Allan
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
import com.google.common.collect.ImmutableSet;
import org.mutabilitydetector.locations.Dotted;

import javax.annotation.concurrent.Immutable;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Collections.newSetFromMap;

public final class CyclicReferences {
    private final Set<CyclicReference> cyclicReferenceCache = newSetFromMap(new ConcurrentHashMap<CyclicReference, Boolean>());

    public Optional<CyclicReference> detectedBetween(Dotted ownerClass, Dotted fieldClass, AnalysisInProgress analysisInProgress) {
        final CyclicReference potentialCyclicReference = new CyclicReference(fieldClass, ownerClass);
        if (cyclicReferenceCache.contains(potentialCyclicReference)) {
            return Optional.of(potentialCyclicReference);
        } else if (fieldClass.equals(ownerClass)) {
            cyclicReferenceCache.add(potentialCyclicReference);
            return Optional.of(potentialCyclicReference);
        } else if (analysisInProgress.contains(fieldClass)) {
            CyclicReference cyclicReference = new CyclicReference(analysisInProgress);
            cyclicReferenceCache.add(cyclicReference);
            return Optional.of(cyclicReference);
        } else {
            return Optional.absent();
        }
    }

    public static CyclicReferences newEmptyMutableInstance() {
        return new CyclicReferences();
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
}
