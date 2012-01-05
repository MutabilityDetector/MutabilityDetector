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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class AnalysisResult {
    public final String dottedClassName;
    public final IsImmutable isImmutable;
    public final Collection<MutableReasonDetail> reasons;

    private AnalysisResult(String dottedClassName, IsImmutable isImmutable, Collection<MutableReasonDetail> reasons) {
        this.dottedClassName = dottedClassName;
        this.isImmutable = isImmutable;
        this.reasons = Collections.unmodifiableCollection(new ArrayList<MutableReasonDetail>(reasons));
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
}
