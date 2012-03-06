package org.mutabilitydetector;

import java.util.HashMap;
import java.util.Map;

public final class PassthroughAnalysisClassLoader implements AnalysisClassLoader {

    private Map<String, Class<?>> classCache = new HashMap<String, Class<?>>();
    
    @Override
    public Class<?> getClass(String dottedClassPath) throws ClassNotFoundException {
        if (classCache.containsKey(dottedClassPath)) { return classCache.get(dottedClassPath); }

        Class<?> toReturn = Class.forName(dottedClassPath);

        classCache.put(dottedClassPath, toReturn);
        
        return toReturn;
    }
}
