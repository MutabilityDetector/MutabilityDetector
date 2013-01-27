/*
 *    Copyright (c) 2008-2013 Graham Allan
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

import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Interpreter;
import org.objectweb.asm.tree.analysis.NonClassloadingSimpleVerifier;
import org.objectweb.asm.tree.analysis.TypeHierarchyReader;

public class NonClassLoadingVerifierFactory implements AsmVerifierFactory {

    private final TypeHierarchyReader typeHierarchyReader;

    public NonClassLoadingVerifierFactory(TypeHierarchyReader typeHierarchyReader) {
        this.typeHierarchyReader = typeHierarchyReader;
    }
    
    @Override
    public Interpreter<BasicValue> interpreter() {
        return new NonClassloadingSimpleVerifier(typeHierarchyReader);
    }

}
