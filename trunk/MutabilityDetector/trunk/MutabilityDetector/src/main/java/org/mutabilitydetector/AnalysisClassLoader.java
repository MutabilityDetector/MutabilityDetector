package org.mutabilitydetector;

public interface AnalysisClassLoader {
    Class<?> loadClass(String dottedClass) throws ClassNotFoundException;
}
