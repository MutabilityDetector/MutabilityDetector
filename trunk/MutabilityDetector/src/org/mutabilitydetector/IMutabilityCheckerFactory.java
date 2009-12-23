package org.mutabilitydetector;

import java.util.Collection;

import org.mutabilitydetector.checkers.IMutabilityChecker;


public interface IMutabilityCheckerFactory {

	public Collection<IMutabilityChecker> createInstances(IAnalysisSession analysisSession);
	
}
