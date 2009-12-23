package se.mutabilitydetector.benchmarks;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.mutabilitydetector.IAnalysisSession.IsImmutable.DEFINITELY_NOT;
import static se.mutabilitydetector.ImmutableAssert.assertNotImmutable;

import org.junit.Before;
import org.junit.Test;

import se.mutabilitydetector.CheckerRunner;
import se.mutabilitydetector.IAnalysisSession;
import se.mutabilitydetector.checkers.MutableTypeToFieldChecker;


public class MutableFieldTest {

	private IAnalysisSession mockSession;
	private MutableTypeToFieldChecker checker;

	@Before
	public void setUp() {
		mockSession = mock(IAnalysisSession.class);
		checker = new MutableTypeToFieldChecker(mockSession);
		
	}
	
	@Test
	public void testCheckerRequestsMutableStatusOfPublishedField() throws Exception {
		when(mockSession.isImmutable(MutableExample.class.getCanonicalName())).thenReturn(DEFINITELY_NOT);
		new CheckerRunner(null).run(checker, MutableByHavingMutableFieldAssigned.class);
		
		verify(mockSession).isImmutable(MutableExample.class.getCanonicalName());
	}
	
	@Test
	public void testCheckerFailsCheckIfAnyFieldsHaveMutableAssignedToThem() throws Exception {
		when(mockSession.isImmutable(MutableExample.class.getCanonicalName())).thenReturn(DEFINITELY_NOT);

		new CheckerRunner(null).run(checker, MutableByHavingMutableFieldAssigned.class);
		
		assertNotImmutable(checker.result());
		assertTrue(checker.reasons().size() > 0);
	}
	
}
