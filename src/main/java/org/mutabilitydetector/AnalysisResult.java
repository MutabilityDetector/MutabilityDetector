package org.mutabilitydetector;

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


import com.google.common.base.Objects;
import org.mutabilitydetector.locations.Dotted;

import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static java.util.Arrays.asList;
import static org.mutabilitydetector.IsImmutable.IMMUTABLE;
import static org.mutabilitydetector.locations.Dotted.dotted;

@Immutable
public final class AnalysisResult {
    public final Dotted className;
    public final IsImmutable isImmutable;
    public final Collection<MutableReasonDetail> reasons;
    public final Collection<AnalysisError> errors;
    private final int hashCode;

    /**
     * The field {@link #className} contains the same class name.
     * Access the same string via <code>className.asString()</code>
     *
     */
    @Deprecated
    public final String dottedClassName;

    private AnalysisResult(
            Dotted className,
            IsImmutable isImmutable,
            Collection<MutableReasonDetail> reasons,
            Collection<AnalysisError> errors) {
        this.className = className;
        this.dottedClassName = className.asString();
        this.isImmutable = isImmutable;
        this.reasons = Collections.unmodifiableList(new ArrayList<MutableReasonDetail>(reasons));
        this.errors = Collections.unmodifiableList(new ArrayList<AnalysisError>(errors));
        
        this.hashCode = Objects.hashCode(className, isImmutable, reasons, errors);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(getClass())
                .add("class", className)
                .add("isImmutable", isImmutable)
                .add("reasons", reasons)
                .add("errors", errors)
                .toString();
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
        AnalysisResult other = (AnalysisResult) obj;
        return className.equals(other.className)
                && isImmutable.equals(other.isImmutable)
                && reasons.equals(other.reasons)
                && errors.equals(other.errors);
    }

    private static void check(IsImmutable isImmutable, Collection<MutableReasonDetail> reasons) {
        if (isImmutable != IMMUTABLE && reasons.isEmpty()) { 
            throw new IllegalArgumentException("Reasons must be given when a class is not " + IsImmutable.IMMUTABLE); 
        }
    }

    public static AnalysisResult analysisResult(Dotted className, IsImmutable isImmutable, MutableReasonDetail... reasons) {
        return analysisResult(className, isImmutable, asList(reasons));
    }

    public static AnalysisResult analysisResult(Dotted className, IsImmutable isImmutable, Collection<MutableReasonDetail> reasons) {
        check(isImmutable, reasons);
        return new AnalysisResult(className, isImmutable, reasons, Collections.<AnalysisError>emptyList());
    }

    public static AnalysisResult analysisResult(
            Dotted className,
            IsImmutable isImmutable,
            Collection<MutableReasonDetail> reasons,
            Collection<AnalysisError> errors) {
        return new AnalysisResult(className, isImmutable, reasons, errors);
    }

    public static AnalysisResult definitelyImmutable(Dotted className) {
        return analysisResult(className, IsImmutable.IMMUTABLE);
    }

    public static AnalysisResult definitelyImmutable(String dottedClassName) {
        return definitelyImmutable(dotted(dottedClassName));
    }

    public static AnalysisResult analysisResult(String dottedClassName, IsImmutable isImmutable) {
        return analysisResult(dotted(dottedClassName), isImmutable);
    }

    public static AnalysisResult analysisResult(String dottedClassName, IsImmutable isImmutable, Collection<MutableReasonDetail> mutableReasonDetails) {
        return analysisResult(dotted(dottedClassName), isImmutable, mutableReasonDetails);
    }

    public static AnalysisResult analysisResult(String className, IsImmutable isImmutable, MutableReasonDetail... reasons) {
        return analysisResult(className, isImmutable, asList(reasons));
    }

    public static AnalysisResult analysisResult(String dottedClassName, IsImmutable isImmutable, MutableReasonDetail reason) {
        return analysisResult(dottedClassName, isImmutable, asList(reason));
    }
}
