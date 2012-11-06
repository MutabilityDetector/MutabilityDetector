package org.mutabilitydetector.asmoverride;

import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Interpreter;

public interface AsmVerifierFactory {
    Interpreter<BasicValue> interpreter();
}
