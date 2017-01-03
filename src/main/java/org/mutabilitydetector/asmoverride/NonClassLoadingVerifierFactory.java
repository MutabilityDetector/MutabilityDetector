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

// TODO: Need to make ASM-NonClassloadingSimpleVerifier available via pom
//import org.mutabilitydetector.asm.CachingTypeHierarchyReader;
//import org.mutabilitydetector.asm.IsAssignableFromCachingTypeHierarchyReader;
//import org.mutabilitydetector.asm.NonClassloadingSimpleVerifier;
//import org.mutabilitydetector.asm.TypeHierarchyReader;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Interpreter;
//
public class NonClassLoadingVerifierFactory implements AsmVerifierFactory {
    @Override
    public Interpreter<BasicValue> interpreter() {
        throw new UnsupportedOperationException("This option will not be supported until ASM-NonClassloadingSimpleVerifier " +
            "is published and can be depended upon.");
    }
}
