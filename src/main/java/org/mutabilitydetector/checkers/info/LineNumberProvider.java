package org.mutabilitydetector.checkers.info;

/*
 * #%L
 * MutabilityDetector
 * %%
 * Copyright (C) 2008 - 2016 Graham Allan
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

import org.mutabilitydetector.locations.Dotted;
import org.mutabilitydetector.locations.line.SourceLocation;

import static org.mutabilitydetector.locations.line.SourceLocation.newUnknownSourceLocation;

public interface LineNumberProvider {
    SourceLocation classLocation(Dotted type);

    SourceLocation fieldLocation(Dotted type, String fieldName);

    enum EmptyProvider implements LineNumberProvider {
        EMPTY_PROVIDER;

        @Override
        public SourceLocation classLocation(Dotted type) {
            return newUnknownSourceLocation();
        }

        @Override
        public SourceLocation fieldLocation(Dotted type, String fieldName) {
            return newUnknownSourceLocation();
        }
    }
}
