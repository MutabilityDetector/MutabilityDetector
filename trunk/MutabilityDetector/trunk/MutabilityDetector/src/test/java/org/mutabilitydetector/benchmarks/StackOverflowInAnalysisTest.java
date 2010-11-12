/*
 * Mutability Detector
 * 
 * Copyright 2009 Graham Allan
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.mutabilitydetector.benchmarks;

import java.lang.reflect.Constructor;

import org.junit.Test;
import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.IAnalysisSession;
import org.mutabilitydetector.benchmarks.MutableByAssigningAbstractTypeToField.AbstractStringContainer;
import org.mutabilitydetector.benchmarks.types.EnumType;

public class StackOverflowInAnalysisTest {

	private IAnalysisSession session = AnalysisSession.createWithCurrentClassPath();

	@Test public void innerClassDoesNotCauseStackOverflowError() throws Exception {
		session.isImmutable(ImmutableExample.class.getName());

		session.isImmutable(AbstractStringContainer.class.getName());
		session.isImmutable(MutableByAssigningAbstractTypeToField.class.getName());
		session.isImmutable(MutableByAssigningAbstractTypeToField.AbstractStringContainer.class.getName());
	}

	@Test public void visitingEnumTypeDoesNotCauseStackOverflowError() throws Exception {
		session.isImmutable(EnumType.class.getName());
	}

	@Test public void analysingThisTestDoesNotCauseStackOverflowError() throws Exception {
		session.isImmutable(this.getClass().getName());
	}

	@Test public void analysingConstructorClassDoesNotCauseStackOverflow() throws Exception {
		// Constructor has a field of type 'Class'
		// Class has a field of type 'Constructor'
		session.isImmutable(Constructor.class.getName());
	}

}
