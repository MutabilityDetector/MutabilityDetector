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


import static org.mutabilitydetector.locations.ClassNameConverter.CONVERTER;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.mutabilitydetector.locations.Dotted;

public final class NamesFromClassResources {

    private final Pattern classNameRegex;

    public NamesFromClassResources(String toAnalyseRegex) {
        this.classNameRegex = Pattern.compile(toAnalyseRegex);
    }

    public List<Dotted> asDotted(String[] findResources) {
        return Arrays.stream(findResources)
                .map(CONVERTER::dotted)
                .filter(classNameRegex.asPredicate())
                .map(Dotted::dotted)
                .collect(Collectors.toList());
    }
}
