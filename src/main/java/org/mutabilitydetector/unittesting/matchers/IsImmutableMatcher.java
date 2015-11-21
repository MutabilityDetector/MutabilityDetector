package org.mutabilitydetector.unittesting.matchers;

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



import static java.lang.String.format;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.IsImmutable;

public final class IsImmutableMatcher extends TypeSafeDiagnosingMatcher<AnalysisResult> {
    private final IsImmutable isImmutable;
    private AnalysisResult result = null;

    private IsImmutableMatcher(IsImmutable isImmutable) {
        this.isImmutable = isImmutable;
    }
    
    public static IsImmutableMatcher hasIsImmutableStatusOf(IsImmutable isImmutable) {
        return new IsImmutableMatcher(isImmutable);
    }

    @Override
    public boolean matchesSafely(AnalysisResult item, Description mismatchDescription) {
        this.result = item;
        mismatchDescription.appendText(format("%s is actually %s%n", item.className.asString(), item.isImmutable));
        return this.isImmutable == item.isImmutable;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(result.className.asString() + " to be " + isImmutable);
    }

}
