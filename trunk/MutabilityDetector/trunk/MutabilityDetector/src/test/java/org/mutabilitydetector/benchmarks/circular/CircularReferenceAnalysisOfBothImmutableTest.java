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
package org.mutabilitydetector.benchmarks.circular;

import static org.mutabilitydetector.AnalysisSession.createWithCurrentClassPath;
import static org.mutabilitydetector.TestUtil.runChecker;

import org.junit.Test;
import org.mutabilitydetector.IAnalysisSession;
import org.mutabilitydetector.checkers.IMutabilityChecker;
import org.mutabilitydetector.checkers.MutableTypeToFieldChecker;

public class CircularReferenceAnalysisOfBothImmutableTest {

	@Test
	public void immutableClassesWithCircularReferencesAreAnalysedCorrectly() throws Exception {
		IAnalysisSession session = createWithCurrentClassPath();
		session.isImmutable(ImmutableClassA.class.getName());
	}
	
	@Test public void mutableFieldCheckerHandlesCircularReferences() throws Exception {
		IAnalysisSession session = createWithCurrentClassPath();
		IMutabilityChecker mutableFieldChecker = new MutableTypeToFieldChecker(session);
		
		runChecker(mutableFieldChecker, ImmutableClassA.class);
	}
}
