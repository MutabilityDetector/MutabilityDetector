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

import com.google.common.base.MoreObjects;

import java.lang.reflect.Field;

import static org.mutabilitydetector.locations.line.SourceLocation.newSourceLocation;
import static org.mutabilitydetector.locations.line.SourceLocation.newUnknownSourceLocation;

public final class LineNumbersUtil {
    // Prevent instantiation.
    private LineNumbersUtil() {
    }

    public static <T> SourceLocation newFieldLocation(Class<T> klass, String fieldName) {
        try {
            Field field = klass.getField(fieldName);
            LineNumbers ln = new LineNumbers(klass);
            String sourceName = ln.getSource();
            Integer lineNumber = MoreObjects.firstNonNull(ln.getLineNumber(field), ln.getFirstLine());
            return newSourceLocation(sourceName, lineNumber);
        } catch (Exception e) {
            return newUnknownSourceLocation();
        }
    }

    public static <T> SourceLocation newClassLocation(Class<T> klass) {
        try {
            LineNumbers ln = new LineNumbers(klass);
            String sourceName = ln.getSource();
            return newSourceLocation(sourceName, ln.getFirstLine());
        } catch (Exception e) {
            return newUnknownSourceLocation();
        }
    }

    public static SourceLocation newUnknownLocation() {
        return SourceLocation.newUnknownSourceLocation();
    }
}
