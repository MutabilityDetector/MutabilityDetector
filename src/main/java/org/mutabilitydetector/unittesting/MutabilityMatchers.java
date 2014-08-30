package org.mutabilitydetector.unittesting;

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



import org.hamcrest.Matcher;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.unittesting.matchers.IsImmutableMatcher;
import org.mutabilitydetector.unittesting.matchers.reasons.NoReasonsAllowed;

public class MutabilityMatchers {

    public static Matcher<MutableReasonDetail> noReasonsAllowed() {
        return new NoReasonsAllowed();
    }

    public static Matcher<AnalysisResult> areImmutable() {
        return IsImmutableMatcher.hasIsImmutableStatusOf(IsImmutable.IMMUTABLE);
    }
    
    public static Matcher<AnalysisResult> areEffectivelyImmutable() {
        return IsImmutableMatcher.hasIsImmutableStatusOf(IsImmutable.EFFECTIVELY_IMMUTABLE);
    }

    public static Matcher<AnalysisResult> areNotImmutable() {
        return IsImmutableMatcher.hasIsImmutableStatusOf(IsImmutable.NOT_IMMUTABLE);
    }
}
