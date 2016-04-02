package org.mutabilitydetector.locations;

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

import org.mutabilitydetector.checkers.info.LineNumberProvider;
import org.mutabilitydetector.locations.CodeLocation.ClassLocation;
import org.mutabilitydetector.locations.CodeLocation.FieldLocation;

import static org.mutabilitydetector.checkers.info.LineNumberProvider.EmptyProvider.EMPTY_PROVIDER;

public final class CodeLocationFactory {
    private final LineNumberProvider provider;

    private CodeLocationFactory(LineNumberProvider provider) {
        this.provider = provider;
    }

    public static CodeLocationFactory createWithLineNumbersInfo(LineNumberProvider provider) {
        return new CodeLocationFactory(provider);
    }

    public static CodeLocationFactory create() {
        return new CodeLocationFactory(EMPTY_PROVIDER);
    }

    public FieldLocation fieldLocation(String fieldName, ClassLocation ownerOfField) {
        return FieldLocation.fieldLocation(fieldName, ownerOfField, provider);
    }
}
