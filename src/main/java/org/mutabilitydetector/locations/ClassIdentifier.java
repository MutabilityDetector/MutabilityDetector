package org.mutabilitydetector.locations;

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



import static org.mutabilitydetector.locations.Dotted.dotted;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class ClassIdentifier {

    private final Dotted dotted;

    private ClassIdentifier(Dotted className) {
        this.dotted = className;
    }

    public Dotted asDotted() {
        return dotted;
    }

    public static ClassIdentifier forClass(Dotted className) {
        return new ClassIdentifier(className);
    }

    public static ClassIdentifier forClass(Slashed className) {
        String slashed = className.asString();
        String dottedString = new ClassNameConverter().dotted(slashed);

        return forClass(dotted(dottedString));
    }
}
