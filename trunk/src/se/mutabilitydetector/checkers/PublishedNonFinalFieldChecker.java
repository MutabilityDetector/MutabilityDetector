package se.mutabilitydetector.checkers;


import static se.mutabilitydetector.IAnalysisSession.IsImmutable.DEFINITELY_NOT;

import org.objectweb.asm.FieldVisitor;

public class PublishedNonFinalFieldChecker extends AbstractMutabilityChecker {

	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		if (!isPrivate(access)) {
			if (!isFinal(access)) {
				reasons.add("Field [" + name + "] is visible outwith this class, and is not declared final.");
				result = DEFINITELY_NOT;
			}
		}
		return super.visitField(access, name, desc, signature, value);
	}
}
