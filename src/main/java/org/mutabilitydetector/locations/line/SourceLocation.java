package org.mutabilitydetector.locations.line;

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

import static com.google.common.base.Preconditions.checkNotNull;

public final class SourceLocation {
    private final String fileName;
    private final int lineNumber;

    private static SourceLocation UNKNOWN = new SourceLocation("", 0);

    public static SourceLocation newUnknownSourceLocation() {
        return UNKNOWN;
    }

    public static SourceLocation newSourceLocation(String fileName, int lineNumber) {
        return new SourceLocation(checkNotNull(fileName), lineNumber);
    }

    private SourceLocation(String fileName, int lineNumber) {
        this.fileName = fileName;
        this.lineNumber = lineNumber;
    }

    @Override
    public String toString() {
        if (UNKNOWN == this) {
            return "";
        }
        return String.format("(%s:%d)", fileName, lineNumber);
    }
}
