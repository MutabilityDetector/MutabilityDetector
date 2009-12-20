package se.mutabilitydetector.checkers;

import java.util.Collection;

import org.objectweb.asm.ClassVisitor;

import se.mutabilitydetector.IAnalysisSession.IsImmutable;

public interface IMutabilityChecker extends ClassVisitor {

	public Collection<String> reasons();

	public IsImmutable result();
	
}
