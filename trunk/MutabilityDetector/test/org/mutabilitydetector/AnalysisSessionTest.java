package org.mutabilitydetector;

import static org.mutabilitydetector.ImmutableAssert.assertImmutable;

import org.junit.Before;
import org.junit.Test;
import org.mutabilitydetector.AllChecksRunner;
import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.CheckerRunnerFactory;
import org.mutabilitydetector.IAnalysisSession;
import org.mutabilitydetector.ICheckerRunnerFactory;
import org.mutabilitydetector.IMutabilityCheckerFactory;
import org.mutabilitydetector.MutabilityCheckerFactory;
import org.mutabilitydetector.IAnalysisSession.IsImmutable;
import org.mutabilitydetector.benchmarks.ImmutableExample;



public class AnalysisSessionTest {

	private IAnalysisSession analysisSession;
	private Class<ImmutableExample> immutableClass;
	
	@Before
	public void setUp() {
		immutableClass = ImmutableExample.class;
	}

	@Test
	public void testAnalysisOfImmutableExampleWillBeRegistered() throws Exception {
		analysisSession = new AnalysisSession(null);
		IMutabilityCheckerFactory mockFactory = new MutabilityCheckerFactory();
		ICheckerRunnerFactory checkerRunnerFactory = new CheckerRunnerFactory(null);
		AllChecksRunner checker = new AllChecksRunner(mockFactory, checkerRunnerFactory, immutableClass);

		checker.runCheckers(analysisSession);
		
		IsImmutable result = analysisSession.isImmutable(immutableClass.getCanonicalName());
		assertImmutable(result);
	}
	
	@Test
	public void testAnalysisWillBeRunForClassesWhenQueriedOnImmutableStatus() throws Exception {
		analysisSession = new AnalysisSession(null);
		IsImmutable result = analysisSession.isImmutable(immutableClass.getCanonicalName());
		assertImmutable(result);
	}
	
	
}
