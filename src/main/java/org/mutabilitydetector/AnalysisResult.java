/*
 *    Copyright (c) 2008-2011 Graham Allan
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.mutabilitydetector;

import static java.util.Arrays.asList;
import static org.mutabilitydetector.IsImmutable.IMMUTABLE;
import static org.mutabilitydetector.locations.Dotted.dotted;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.annotation.concurrent.Immutable;

import org.mutabilitydetector.locations.Dotted;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;

@Immutable
public final class AnalysisResult {
    public final String dottedClassName;
    public final IsImmutable isImmutable;
    public final Collection<MutableReasonDetail> reasons;
    private final int hashCode;

    private AnalysisResult(String dottedClassName, IsImmutable isImmutable, Collection<MutableReasonDetail> reasons) {
        this.dottedClassName = dottedClassName;
        this.isImmutable = isImmutable;
        this.reasons = Collections.unmodifiableList(new ArrayList<MutableReasonDetail>(reasons));
        
        this.hashCode = Objects.hashCode(dottedClassName, isImmutable, reasons);
    }

    
    @Override
    public String toString() {
        return Objects.toStringHelper(getClass())
                .add("class", dottedClassName)
                .add("isImmutable", isImmutable)
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
        return dottedClassName.equals(other.dottedClassName)
                && isImmutable.equals(other.isImmutable)
                && reasons.equals(other.reasons);
    }

    public static AnalysisResult analysisResult(String dottedClassName, IsImmutable isImmutable, MutableReasonDetail... reasons) {
        return analysisResult(dottedClassName, isImmutable, asList(reasons));
    }
    
    public static AnalysisResult analysisResult(String dottedClassName, IsImmutable isImmutable, Collection<MutableReasonDetail> reasons) {
        check(isImmutable, reasons);
        return new AnalysisResult(dottedClassName, isImmutable, reasons);
    }

    private static void check(IsImmutable isImmutable, Collection<MutableReasonDetail> reasons) {
        if (isImmutable != IMMUTABLE && reasons.isEmpty()) { 
            throw new IllegalArgumentException("Reasons must be given when a class is not " + IsImmutable.IMMUTABLE); 
        }
    }

    public static AnalysisResult definitelyImmutable(String dottedClassName) {
        return analysisResult(dottedClassName, IsImmutable.IMMUTABLE);
    }
    
    public static final Predicate<AnalysisResult> forClass(final Dotted className) {
        return new Predicate<AnalysisResult>() { @Override public boolean apply(AnalysisResult input) {
            return input.dottedClassName.equals(className.asString());
        }};
    }

    public static final Function<AnalysisResult, Dotted> TO_DOTTED_CLASSNAME = new Function<AnalysisResult, Dotted>() {
        @Override public Dotted apply(AnalysisResult input) {
            return dotted(input.dottedClassName);
        }
    };

    public static final Function<AnalysisResult, String> TO_CLASSNAME = new Function<AnalysisResult, String>() {
        @Override public String apply(AnalysisResult input) {
            return input.dottedClassName;
        }
    };
    
}
