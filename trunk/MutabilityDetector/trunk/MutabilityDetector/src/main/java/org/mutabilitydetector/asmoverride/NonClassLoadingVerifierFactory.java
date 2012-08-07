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
