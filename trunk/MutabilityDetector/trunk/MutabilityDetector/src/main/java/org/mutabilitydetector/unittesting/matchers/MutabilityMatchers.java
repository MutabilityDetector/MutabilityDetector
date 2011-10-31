/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector.unittesting.matchers;

import org.mutabilitydetector.IAnalysisSession.IsImmutable;

public class MutabilityMatchers {

    public static AnalysisResultMatcher noWarningsAllowed() {
        return new NoWarningsAllowedMatcher();
    }

    public static IsImmutableMatcher areImmutable() {
        return new IsImmutableMatcher(IsImmutable.IMMUTABLE);
    }

    public static IsImmutableMatcher areNotImmutable() {
        return new IsImmutableMatcher(IsImmutable.NOT_IMMUTABLE);
    }
}
