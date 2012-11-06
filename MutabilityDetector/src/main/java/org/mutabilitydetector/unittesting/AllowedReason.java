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

package org.mutabilitydetector.unittesting;

import static com.google.common.collect.Iterables.transform;
import static java.util.Arrays.asList;
import static org.mutabilitydetector.locations.Dotted.CLASS_TO_DOTTED;
import static org.mutabilitydetector.locations.Dotted.dotted;
import static org.mutabilitydetector.locations.Dotted.fromClass;

import org.hamcrest.Matcher;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.unittesting.matchers.reasons.AllowingForSubclassing;
import org.mutabilitydetector.unittesting.matchers.reasons.AllowingNonFinalFields;
import org.mutabilitydetector.unittesting.matchers.reasons.NoReasonsAllowedMatcher;
import org.mutabilitydetector.unittesting.matchers.reasons.ProvidedOtherClass;

public final class AllowedReason {

    private AllowedReason() { }

    public static ProvidedOtherClass provided(String dottedClassName) {
        return ProvidedOtherClass.provided(dotted(dottedClassName));
    }

    public static ProvidedOtherClass provided(Class<?> clazz) {
        return ProvidedOtherClass.provided(fromClass(clazz));
    }

    public static ProvidedOtherClass provided(Class<?>... classes) {
        return ProvidedOtherClass.provided(transform(asList(classes), CLASS_TO_DOTTED));
    }

    public static AllowingForSubclassing allowingForSubclassing() {
        return new AllowingForSubclassing();
    }

    public static Matcher<MutableReasonDetail> allowingNonFinalFields() {
        return new AllowingNonFinalFields();
    }

    public static Matcher<MutableReasonDetail> noReasonsAllowed() {
        return NoReasonsAllowedMatcher.noReasonsAllowed();
    }


}
