package org.mutabilitydetector.classloading;

public interface AnalysisClassLoader {
    Class<?> loadClass(String dottedClass) throws ClassNotFoundException;
}
