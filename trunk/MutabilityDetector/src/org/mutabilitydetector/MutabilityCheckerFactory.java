/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */
package org.mutabilitydetector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.mutabilitydetector.checkers.AbstractTypeToFieldChecker;
import org.mutabilitydetector.checkers.FinalClassChecker;
import org.mutabilitydetector.checkers.IMutabilityChecker;
import org.mutabilitydetector.checkers.InherentTypeMutabilityChecker;
import org.mutabilitydetector.checkers.InheritedMutabilityChecker;
import org.mutabilitydetector.checkers.MutableTypeToFieldChecker;
import org.mutabilitydetector.checkers.PublishedNonFinalFieldChecker;
import org.mutabilitydetector.checkers.SetterMethodChecker;


public class MutabilityCheckerFactory implements IMutabilityCheckerFactory {

	@Override
	public Collection<IMutabilityChecker> createInstances(IAnalysisSession analysisSession) {
		Collection<IMutabilityChecker> checkers = new ArrayList<IMutabilityChecker>();
		checkers.add(new FinalClassChecker());
		checkers.add(new AbstractTypeToFieldChecker());
		checkers.add(new PublishedNonFinalFieldChecker());
		checkers.add(new SetterMethodChecker());
		checkers.add(new MutableTypeToFieldChecker(analysisSession));
		checkers.add(new InherentTypeMutabilityChecker());
		checkers.add(new InheritedMutabilityChecker(analysisSession));
//		checkers.add(new NoCopyOfFieldChecker()); - or whatever it's going to be called.
		return Collections.unmodifiableCollection(checkers);
	}

}
