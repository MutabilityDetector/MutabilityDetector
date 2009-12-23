package se.mutabilitydetector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import se.mutabilitydetector.checkers.AbstractTypeToFieldChecker;
import se.mutabilitydetector.checkers.FinalClassChecker;
import se.mutabilitydetector.checkers.IMutabilityChecker;
import se.mutabilitydetector.checkers.InherentTypeMutabilityChecker;
import se.mutabilitydetector.checkers.InheritedMutabilityChecker;
import se.mutabilitydetector.checkers.MutableTypeToFieldChecker;
import se.mutabilitydetector.checkers.PublishedNonFinalFieldChecker;
import se.mutabilitydetector.checkers.SetterMethodChecker;

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
