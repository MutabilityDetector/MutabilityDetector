/* 
 * Mutability Detector
 *
 * Copyright 2009 Graham Allan
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.mutabilitydetector.benchmarks;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mutabilitydetector.IAnalysisSession.IsImmutable.DEFINITELY_NOT;
import static org.mutabilitydetector.ImmutableAssert.assertDefinitelyNotImmutable;
import static org.mutabilitydetector.ImmutableAssert.assertImmutable;
import static org.mutabilitydetector.ImmutableAssert.assertNotImmutable;
import static org.mutabilitydetector.TestMatchers.hasNoReasons;
import static org.mutabilitydetector.TestMatchers.hasReasons;
import static org.mutabilitydetector.TestUtil.analysisDatabase;
import static org.mutabilitydetector.TestUtil.runChecker;
import static org.mutabilitydetector.checkers.info.AnalysisDatabase.TYPE_STRUCTURE;

import org.junit.Before;
import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.IAnalysisSession;
import org.mutabilitydetector.benchmarks.MutableByAssigningAbstractTypeToField.AbstractStringContainer;
import org.mutabilitydetector.checkers.MutableTypeToFieldChecker;
import org.mutabilitydetector.checkers.info.TypeStructureInformation;

public class MutableTypeToFieldCheckerTest {

	private IAnalysisSession mockSession;
	private MutableTypeToFieldChecker checker;
	private AnalysisResult result;

	@Before public void setUp() {
		mockSession = mock(IAnalysisSession.class);
		TypeStructureInformation info = analysisDatabase().requestInformation(TYPE_STRUCTURE);
		checker = new MutableTypeToFieldChecker(mockSession, info);
	}
	
	@Test public void requestsMutableStatusOfPublishedField() throws Exception {
		when(mockSession.isImmutable(MutableExample.class.getCanonicalName())).thenReturn(DEFINITELY_NOT);
		runChecker(checker, MutableByHavingMutableFieldAssigned.class);
		
		verify(mockSession).isImmutable(MutableExample.class.getCanonicalName());
	}
	
	@Test public void failsCheckWhenMutableTypeIsAssignedToField() throws Exception {
		when(mockSession.isImmutable(MutableExample.class.getCanonicalName())).thenReturn(DEFINITELY_NOT);
		result = runChecker(checker, MutableByHavingMutableFieldAssigned.class);
		
		assertThat(checker, hasReasons());
		assertNotImmutable(result);
	}
	
	@Test public void failsCheckIfAnyFieldsHaveMutableAssignedToThem() throws Exception {
		when(mockSession.isImmutable(MutableExample.class.getCanonicalName())).thenReturn(DEFINITELY_NOT);

		result = runChecker(checker, MutableByHavingMutableFieldAssigned.class);
		
		assertThat(checker, hasReasons());
		assertNotImmutable(result);
	}
	
	@Test public void instanceFieldWhichHasAMutatedArrayIsMutable() throws Exception {
		result = runChecker(checker, MutableByHavingArrayTypeAsField.class);
		assertDefinitelyNotImmutable(result);
	}
	
	@Test public void staticFieldWhichHasAMutatedArrayIsImmutable() throws Exception {
		result = runChecker(checker, ImmutableWhenArrayFieldIsStatic.class);
		assertImmutable(result);
	}
	
	@Test public void doesNotRaiseErrorForAbstractTypeSinceThisIsRaisedByAbstractTypeToFieldChecker() throws Exception {
		when(mockSession.isImmutable(AbstractStringContainer.class.getName())).thenReturn(DEFINITELY_NOT);
		result = runChecker(checker, MutableByAssigningAbstractTypeToField.class);
		
		assertThat(checker, hasNoReasons());
		assertImmutable(result);
	}
	
}
