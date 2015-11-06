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

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import org.mutabilitydetector.locations.Dotted;

public final class AnalysisInProgress {
    private final ImmutableList<Dotted> inProgress;

    private AnalysisInProgress() {
        this.inProgress = ImmutableList.of();
    }

    private AnalysisInProgress(ImmutableList<Dotted> inProgress) {
        this.inProgress = inProgress;
    }

    public static AnalysisInProgress noAnalysisUnderway() { return new AnalysisInProgress(); }

    public boolean contains(Dotted clazz) { return inProgress.contains(clazz); }

    public AnalysisInProgress analysisStartedFor(Dotted clazz) {
        return new AnalysisInProgress(ImmutableList.<Dotted>builder().addAll(inProgress).add(clazz).build());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnalysisInProgress that = (AnalysisInProgress) o;
        return Objects.equal(inProgress, that.inProgress);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(inProgress);
    }

    @Override
    public String toString() {
        return "AnalysisInProgress{" +
                "inProgress=" + inProgress +
                '}';
    }
}
