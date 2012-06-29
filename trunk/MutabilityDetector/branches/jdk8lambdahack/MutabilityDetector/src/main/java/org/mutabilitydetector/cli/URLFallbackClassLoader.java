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

import java.net.URLClassLoader;

import org.mutabilitydetector.AnalysisClassLoader;
import org.mutabilitydetector.ClassForNameWrapper;

public final class URLFallbackClassLoader implements AnalysisClassLoader {

    private final URLClassLoader urlClassLoader;
    private final ClassForNameWrapper classForNameWrapper;

    public URLFallbackClassLoader(URLClassLoader urlClassLoader, ClassForNameWrapper classForNameWrapper) {
        this.urlClassLoader = urlClassLoader;
        this.classForNameWrapper = classForNameWrapper;
    }

    @Override
    public Class<?> loadClass(final String dottedClass) throws ClassNotFoundException {
        Class<?> toReturn;
        try {
            toReturn = urlClassLoader.loadClass(dottedClass);
        } catch (ClassNotFoundException e) {
            toReturn = classForNameWrapper.loadClass(dottedClass);
        }
        return toReturn;
    }
}
