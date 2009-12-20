package se.mutabilitydetector.checkers;

import static se.mutabilitydetector.IAnalysisSession.IsImmutable.DEFINITELY;
import static se.mutabilitydetector.IAnalysisSession.IsImmutable.MAYBE;

import org.objectweb.asm.Opcodes;

public class FinalClassChecker extends AbstractMutabilityChecker {

	
	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		if((access & Opcodes.ACC_FINAL) == 0) {
			result = MAYBE;
			reasons.add("Class is not declared final.");
		} else {
			result = DEFINITELY;
		}
		
	}

}
