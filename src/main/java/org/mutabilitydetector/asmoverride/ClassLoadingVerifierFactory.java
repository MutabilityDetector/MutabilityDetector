package org.mutabilitydetector.asmoverride;

/*
 * #%L
 * MutabilityDetector
 * %%
 * Copyright (C) 2008 - 2014 Graham Allan
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



import org.mutabilitydetector.classloading.AnalysisClassLoader;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Interpreter;
import org.objectweb.asm.tree.analysis.SimpleVerifier;

public class ClassLoadingVerifierFactory implements AsmVerifierFactory {

    private final AnalysisClassLoader classLoader;

    public ClassLoadingVerifierFactory(AnalysisClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    
    @Override
    public Interpreter<BasicValue> interpreter() {
        SimpleVerifier simpleVerifier = new SimpleVerifier();
        simpleVerifier.setClassLoader(new ClassLoader() {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                return classLoader.loadClass(name);
            }
        });
        return simpleVerifier;
    }

}
