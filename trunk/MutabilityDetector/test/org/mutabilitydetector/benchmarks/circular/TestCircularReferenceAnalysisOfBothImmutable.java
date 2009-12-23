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

import org.junit.Test;
import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.CheckerRunner;
import org.mutabilitydetector.IAnalysisSession;
import org.mutabilitydetector.benchmarks.circular.ImmutableClassA;
import org.mutabilitydetector.checkers.IMutabilityChecker;
import org.mutabilitydetector.checkers.MutableTypeToFieldChecker;




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
