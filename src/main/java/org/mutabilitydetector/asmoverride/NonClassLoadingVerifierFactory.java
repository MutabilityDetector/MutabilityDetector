package org.mutabilitydetector.asmoverride;

/*-
 * #%L
 * MutabilityDetector
 * %%
 * Copyright (C) 2008 - 2016 Graham Allan
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import com.google.classpath.ClassPath;
import org.mutabilitydetector.asm.tree.analysis.NonClassloadingSimpleVerifier;
import org.mutabilitydetector.asm.typehierarchy.ConcurrentMapCachingTypeHierarchyReader;
import org.mutabilitydetector.asm.typehierarchy.IsAssignableFromCachingTypeHierarchyReader;
import org.mutabilitydetector.asm.typehierarchy.TypeHierarchyReader;
import org.mutabilitydetector.locations.Dotted;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Interpreter;

import java.io.IOException;

public class NonClassLoadingVerifierFactory implements AsmVerifierFactory {

    private final FromConfiguredClassPathTypeHierarchyReader typeHierarchyReader;

    public NonClassLoadingVerifierFactory(ClassPath classPath) {
        this.typeHierarchyReader = new FromConfiguredClassPathTypeHierarchyReader(classPath);
    }

    @Override
    public Interpreter<BasicValue> interpreter() {
        return new NonClassloadingSimpleVerifier(
            new IsAssignableFromCachingTypeHierarchyReader(
                new ConcurrentMapCachingTypeHierarchyReader(
                    typeHierarchyReader)));
    }

    private static final class FromConfiguredClassPathTypeHierarchyReader extends TypeHierarchyReader {

        private final ClassPath classPath;

        public FromConfiguredClassPathTypeHierarchyReader(ClassPath classPath) {
            this.classPath = classPath;
        }

        @Override
        protected ClassReader reader(Type t) throws IOException {
            return new ClassReader(classPath.getResourceAsStream(Dotted.fromType(t).asResource()));
        }
    }
}
