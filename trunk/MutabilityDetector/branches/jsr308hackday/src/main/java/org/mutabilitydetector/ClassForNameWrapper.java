package org.mutabilitydetector;

public final class ClassForNameWrapper implements AnalysisClassLoader {

    @Override
    public Class<?> loadClass(String dottedClass) throws ClassNotFoundException {
        return Class.forName(dottedClass);
    }

}
