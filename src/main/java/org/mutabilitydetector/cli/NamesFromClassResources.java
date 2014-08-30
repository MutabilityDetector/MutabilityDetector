package org.mutabilitydetector.cli;

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



import static com.google.common.base.Predicates.containsPattern;
import static java.util.Arrays.asList;
import static org.mutabilitydetector.locations.ClassNameConverter.TO_DOTTED_STRING;
import static org.mutabilitydetector.locations.Dotted.STRING_NAME_TO_DOTTED;

import java.util.List;

import org.mutabilitydetector.locations.Dotted;

import com.google.common.collect.FluentIterable;

public final class NamesFromClassResources {

    private final String classNameRegex;

    public NamesFromClassResources(String toAnalyseRegex) {
        this.classNameRegex = toAnalyseRegex;
    }

    public List<Dotted> asDotted(String[] findResources) {
        return FluentIterable.from(asList(findResources))
                .transform(TO_DOTTED_STRING)
                .filter(containsPattern(classNameRegex))
                .transform(STRING_NAME_TO_DOTTED).toImmutableList();
    }

}