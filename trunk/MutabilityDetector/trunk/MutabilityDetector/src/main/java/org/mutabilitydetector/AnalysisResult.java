/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector;

import static java.util.Arrays.asList;
import static org.mutabilitydetector.IsImmutable.IMMUTABLE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


public final class AnalysisResult {
    public final String dottedClassName;
    public final IsImmutable isImmutable;
    public final Collection<CheckerReasonDetail> reasons;

    private AnalysisResult(String dottedClassName, IsImmutable isImmutable, Collection<CheckerReasonDetail> reasons) {
        this.dottedClassName = dottedClassName;
        this.isImmutable = isImmutable;
        this.reasons = Collections.unmodifiableCollection(new ArrayList<CheckerReasonDetail>(reasons));
    }


    public static AnalysisResult analysisResult(String dottedClassName, IsImmutable isImmutable, CheckerReasonDetail... reasons) {
        return analysisResult(dottedClassName, isImmutable, asList(reasons));
    }
    
    public static AnalysisResult analysisResult(String dottedClassName, IsImmutable isImmutable, Collection<CheckerReasonDetail> reasons) {
        check(isImmutable, reasons);
        return new AnalysisResult(dottedClassName, isImmutable, reasons);
    }

    private static void check(IsImmutable isImmutable, Collection<CheckerReasonDetail> reasons) {
        if (isImmutable != IMMUTABLE && reasons.isEmpty()) { 
            throw new IllegalArgumentException("Reasons must be given when a class is not " + IsImmutable.IMMUTABLE); 
        }
    }

    public static AnalysisResult definitelyImmutable(String dottedClassName) {
        return analysisResult(dottedClassName, IsImmutable.IMMUTABLE);
    }
}
