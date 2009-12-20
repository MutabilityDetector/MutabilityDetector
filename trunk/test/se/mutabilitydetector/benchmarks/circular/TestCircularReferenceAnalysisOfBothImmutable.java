package se.mutabilitydetector.benchmarks.circular;

import org.junit.Test;

import se.mutabilitydetector.AnalysisSession;
import se.mutabilitydetector.CheckerRunner;
import se.mutabilitydetector.IAnalysisSession;
import se.mutabilitydetector.checkers.IMutabilityChecker;
import se.mutabilitydetector.checkers.MutableTypeToFieldChecker;


public class TestCircularReferenceAnalysisOfBothImmutable {

	@Test
	public void testImmutableClassesWithCircularReferencesAreAnalysedCorrectly() throws Exception {
		IAnalysisSession session = new AnalysisSession(null);
		session.isImmutable(ImmutableClassA.class.getName());
	}
	
	
	@Test
	public void testMutableFieldCheckerHandlesCircularReferences() throws Exception {
		// finer grained, because it's this checker that causes the problem
		IAnalysisSession session = new AnalysisSession(null);
		IMutabilityChecker mutableFieldChecker = new MutableTypeToFieldChecker(session);
		
		new CheckerRunner(null).run(mutableFieldChecker, ImmutableClassA.class);
		
	}
}
