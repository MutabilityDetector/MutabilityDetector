package org.mutabilitydetector.locations.line;

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
            return newSourceLocation(klass, sourceName, lineNumber);
        } catch (Exception e) {
            return newUnknownSourceLocation();
        }
    }

    public static <T> SourceLocation newClassLocation(Class<T> klass) {
        try {
            LineNumbers ln = new LineNumbers(klass);
            String sourceName = ln.getSource();
            return newSourceLocation(klass, sourceName, ln.getFirstLine());
        } catch (Exception e) {
            return newUnknownSourceLocation();
        }
    }
}
