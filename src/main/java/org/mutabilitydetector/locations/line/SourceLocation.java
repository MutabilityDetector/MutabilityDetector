package org.mutabilitydetector.locations.line;

import static com.google.common.base.Preconditions.checkNotNull;

public final class SourceLocation {
    private final String className;
    private final String fileName;
    private final int lineNumber;

    private static SourceLocation UNKNOWN = new SourceLocation(null, null, 0);

    static SourceLocation newUnknownSourceLocation() {
        return UNKNOWN;
    }

    static SourceLocation newSourceLocation(Class<?> type, String fileName, int lineNumber) {
        return new SourceLocation(checkNotNull(type), checkNotNull(fileName), lineNumber);
    }

    private SourceLocation(Class<?> type, String fileName, int lineNumber) {
        this.className = type.getName();
        this.fileName = fileName;
        this.lineNumber = lineNumber;
    }

    @Override
    public String toString() {
        if (UNKNOWN == this) {
            return "";
        }
        return String.format("at %s(%s:%d)", className, fileName, lineNumber);
    }
}
