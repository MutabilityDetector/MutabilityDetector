/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */
package org.mutabilitydetector.benchmarks;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mutabilitydetector.IAnalysisSession.IsImmutable.DEFINITELY_NOT;
import static org.mutabilitydetector.ImmutableAssert.assertNotImmutable;

import org.junit.Before;
import org.junit.Test;
import org.mutabilitydetector.CheckerRunner;
import org.mutabilitydetector.IAnalysisSession;
import org.mutabilitydetector.benchmarks.MutableByHavingMutableFieldAssigned;
import org.mutabilitydetector.benchmarks.MutableExample;
import org.mutabilitydetector.checkers.MutableTypeToFieldChecker;




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
