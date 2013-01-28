/*
 *    Copyright (c) 2008-2011 Graham Allan
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.mutabilitydetector.cli;

import static java.lang.String.format;

import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

public class URLFallbackClassLoader {

    private final URLClassLoader urlClassLoader;
    private Map<String, Class<?>> classCache = new HashMap<String, Class<?>>();

    public URLFallbackClassLoader() {
        this.urlClassLoader = getURLClassLoader();
    }

    public Class<?> getClass(String dottedClassPath) throws ClassNotFoundException {
        if (classCache.containsKey(dottedClassPath)) { return classCache.get(dottedClassPath); }

        Class<?> toReturn;
        try {
            toReturn = fromURLClassLoader(dottedClassPath);
        } catch (ClassNotFoundException e) {
            toReturn = fromJVMClassLoader(dottedClassPath);
        }

        classCache.put(dottedClassPath, toReturn);
        return toReturn;
    }

    private Class<?> fromJVMClassLoader(String dottedClassPath) throws ClassNotFoundException {
        return Class.forName(dottedClassPath);
    }

    private Class<?> fromURLClassLoader(String dottedClassPath) throws ClassNotFoundException {
        return urlClassLoader.loadClass(dottedClassPath);
    }

    private URLClassLoader getURLClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader instanceof URLClassLoader) {
            return (URLClassLoader) classLoader;
        } 

        String message = format("Expected currentThread().getContextClassLoader() to return a URLClassLoader, " + "but returned %s.",
                                classLoader);
        throw new ClassCastException(message);
    }
}
