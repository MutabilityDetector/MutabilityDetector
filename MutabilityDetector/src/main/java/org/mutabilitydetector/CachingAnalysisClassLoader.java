package org.mutabilitydetector;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public final class CachingAnalysisClassLoader implements AnalysisClassLoader {
    
    private final Cache<String, Class<?>> cache = CacheBuilder.newBuilder().recordStats().build();
    private final AnalysisClassLoader classLoader;
    
    public CachingAnalysisClassLoader(AnalysisClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public Class<?> loadClass(final String dottedClass) throws ClassNotFoundException {
        try {
            return cache.get(dottedClass, new Callable<Class<?>>() {
                @Override public Class<?> call() throws Exception {
                    return classLoader.loadClass(dottedClass);
                }
            });
        } catch (ExecutionException e) {
            throw new ClassNotFoundException("Error loading class: " + dottedClass, e.getCause());
        }
    }

}
