package se.mutabilitydetector;

import java.util.Collection;

import se.mutabilitydetector.checkers.IMutabilityChecker;

public interface IMutabilityCheckerFactory {

	public Collection<IMutabilityChecker> createInstances(IAnalysisSession analysisSession);
	
}
