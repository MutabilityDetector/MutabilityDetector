package org.mutabilitydetector;

public interface AnalysisClassLoader {
    Class<?> getClass(String dottedClassPath) throws ClassNotFoundException;
}
