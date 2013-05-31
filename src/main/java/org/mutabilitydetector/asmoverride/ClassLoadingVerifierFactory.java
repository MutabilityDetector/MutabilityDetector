package org.mutabilitydetector.asmoverride;

import org.mutabilitydetector.classloading.AnalysisClassLoader;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Interpreter;

public class ClassLoadingVerifierFactory implements AsmVerifierFactory {

    private final AnalysisClassLoader classLoader;

    public ClassLoadingVerifierFactory(AnalysisClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    
    @Override
    public Interpreter<BasicValue> interpreter() {
        return new CustomClassLoadingSimpleVerifier(classLoader);
    }

}
