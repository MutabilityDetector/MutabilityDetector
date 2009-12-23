package org.mutabilitydetector.checkers;

import java.util.Collection;

import org.mutabilitydetector.IAnalysisSession.IsImmutable;
import org.objectweb.asm.ClassVisitor;


public interface IMutabilityChecker extends ClassVisitor {

	public Collection<String> reasons();

	public IsImmutable result();
	
}
