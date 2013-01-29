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
package org.mutabilitydetector.asmoverride;

import org.mutabilitydetector.cli.URLFallbackClassLoader;
import org.mutabilitydetector.locations.ClassNameConvertor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.analysis.SimpleVerifier;

public final class CustomClassLoadingSimpleVerifier extends SimpleVerifier {

    private final URLFallbackClassLoader classLoader;
    private final ClassNameConvertor classNameConverter = new ClassNameConvertor();

    public CustomClassLoadingSimpleVerifier() {
        classLoader = new URLFallbackClassLoader();
    }

    @Override
    protected Class<?> getClass(Type t) {
        String className;

        try {
            if (t.getSort() == Type.ARRAY) {
                className = classNameConverter.dotted(t.getDescriptor());
            } else {
                className = t.getClassName();
            }
            return classLoader.getClass(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
