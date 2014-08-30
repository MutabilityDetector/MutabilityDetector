package org.mutabilitydetector.benchmarks.mutabletofield.jdktypefields;

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

import java.util.List;

public final class HasCollectionField {
    private final List<String> myStrings;

    public HasCollectionField(List<String> strings) {
        List<String> copy = copyIntoNewList(strings);
        List<String> unmodifiable = wrapWithUnmodifiable(copy);
        this.myStrings = unmodifiable;
    }

    private List<String> wrapWithUnmodifiable(List<String> strings) {
        return strings;
    }

    private List<String> copyIntoNewList(List<String> strings) {
        return strings;
    }
    
    public List<String> getMyStrings() {
        return myStrings;
    }
}