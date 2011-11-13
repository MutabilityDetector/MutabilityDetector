/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector.unittesting;

import static org.mutabilitydetector.locations.Dotted.dotted;
import static org.mutabilitydetector.locations.Dotted.fromClass;

import org.hamcrest.Matcher;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.unittesting.matchers.reasons.AllowingForSubclassing;
import org.mutabilitydetector.unittesting.matchers.reasons.NoReasonsAllowedMatcher;
import org.mutabilitydetector.unittesting.matchers.reasons.ProvidedOtherClass;

public class AllowedReason {

    private AllowedReason() { }

    public static ProvidedOtherClass provided(String dottedClassName) {
        return ProvidedOtherClass.provided(dotted(dottedClassName));
    }

    public static ProvidedOtherClass provided(Class<?> clazz) {
        return ProvidedOtherClass.provided(fromClass(clazz));
    }

    public static AllowingForSubclassing allowingForSubclassing() {
        return new AllowingForSubclassing();
    }

    public static Matcher<MutableReasonDetail> noReasonsAllowed() {
        return NoReasonsAllowedMatcher.noWarningsAllowed();
    }

}
