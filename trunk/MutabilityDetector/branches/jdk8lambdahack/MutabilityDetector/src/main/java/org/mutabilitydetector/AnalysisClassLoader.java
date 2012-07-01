package org.mutabilitydetector;

public interface AnalysisClassLoader {
    Class<?> loadClass(String dottedClass) throws ClassNotFoundException;
    
    final AnalysisClassLoader CLASS_FOR_NAME_LOADER = dottedClass -> Class.forName(dottedClass);
}
